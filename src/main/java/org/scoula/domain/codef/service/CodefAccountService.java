package org.scoula.domain.codef.service;

import static org.scoula.domain.codef.exception.CodefErrorCode.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;

import org.scoula.domain.codef.dto.request.CodefConnectedIdRequest;
import org.scoula.domain.codef.dto.request.CodefVerifyCodeRequest;
import org.scoula.domain.codef.dto.response.CodefVerifyCodeResponse;
import org.scoula.domain.member.entity.Member;
import org.scoula.domain.member.service.MemberService;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.scoula.global.redis.util.RedisUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CodefAccountService {

	private static final String CONNECTED_ID_URL = "https://development.codef.io/v1/account/create";
	private static final String VERIFY_CODE_URL = "https://development.codef.io/v1/kr/bank/a/account/transfer-authentication";
	private static final String REDIS_ACCESS_TOKEN_KEY = "codef_access_token";
	private static final String CIPHER_TRANSFORMATION = "RSA/ECB/PKCS1Padding";
	private static final String KEY_ALGORITHM = "RSA";
	private static final String REDIS_CODEF_VERIFY_KEY = "codef_verify_key";

	@Value("${codef.public.key}")
	private String publicKey;

	private final RedisUtil redisUtil;
	private final MemberService memberService;

	public void verifyCode(CodefVerifyCodeRequest codefVerifyCodeRequest, Long memberId, HttpServletRequest request) {
		String redisAuthCode = redisUtil.get(REDIS_CODEF_VERIFY_KEY + memberId);

		if (!redisAuthCode.equals(codefVerifyCodeRequest.authCode())) {
			throw new CustomException(CODEF_VERIFICATION_CODE_FAIL, LogLevel.WARNING, "코데프 1원 안증에 실패하였습니다.",
				Common.builder()
					.srcIp(request.getRemoteAddr())
					.callApiPath(request.getRequestURI())
					.apiMethod(request.getMethod())
					.deviceInfo(request.getHeader("user-agent"))
					.build());
		}
	}

	public CodefVerifyCodeResponse sendVerifyCode(CodefConnectedIdRequest req, Long memberId,
		HttpServletRequest request) {
		try {
			HttpURLConnection conn = getAuthorizedConnection(VERIFY_CODE_URL, request);

			String requestBody = String.format("""
				{
				  "organization": "%s",
				  "account": "%s",
				  "inPrintType": "0"
				}
				""", req.bankType().getCode(), req.accountNumber());

			try (OutputStream os = conn.getOutputStream()) {
				os.write(requestBody.getBytes(StandardCharsets.UTF_8));
			}

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String decoded = readResponse(conn);
				JsonNode root = new ObjectMapper().readTree(decoded);
				String authCode = root.path("data").path("authCode").asText();
				redisUtil.set(REDIS_CODEF_VERIFY_KEY + memberId, authCode, 5);
				return new CodefVerifyCodeResponse(authCode);
			} else {
				String error = readErrorResponse(conn);
				log.info("1원 인증 코드 전송 에러 = " + error);
				throw new Error(error);
			}

		} catch (Exception e) {
			throw new Error(e.toString());
		}
	}

	public void requestConnectedIdCreate(CodefConnectedIdRequest req, Member member, HttpServletRequest request) {
		if (member.getConnectedId() != null)
			return;

		try {
			HttpURLConnection conn = getAuthorizedConnection(CONNECTED_ID_URL, request);
			String encryptedPwd = Encryption(req.bankPassword());
			String requestBody = String.format("""
				{
				  "accountList":[
					{
					  "countryCode": "KR",
					  "businessType": "BK",
					  "clientType": "P",
					  "organization": "%s",
					  "loginType": "1",
					  "id": "%s",
					  "password": "%s"
					}
				  ]
				}
				""", req.bankType().getCode(), req.bankId(), encryptedPwd);

			try (OutputStream os = conn.getOutputStream()) {
				os.write(requestBody.getBytes(StandardCharsets.UTF_8));
			}

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				String decoded = readResponse(conn);
				JsonNode root = new ObjectMapper().readTree(decoded);
				String connectedId = root.path("data").path("connectedId").asText();
				if (connectedId.isEmpty() || connectedId.isBlank()) {
					throw new RuntimeException();
				}
				memberService.updateConnectedId(member.getMemberId(), connectedId);
			} else {
				String error = readErrorResponse(conn);
				log.info("커넥티드 코드 요청 에러 = " + error);
				throw new Error(error);
			}
		} catch (RuntimeException e) {
			throw new CustomException(BANK_LOGIN_FAIL, LogLevel.WARNING, null, Common.builder()
				.srcIp(request.getRemoteAddr())
				.callApiPath(request.getRequestURI())
				.apiMethod(request.getMethod())
				.deviceInfo(request.getHeader("host-agent"))
				.memberId(String.valueOf(member.getMemberId()))
				.build(), "은행 로그인 실패 에러");
		} catch (Exception e) {
			throw new Error(e.toString());
		}
	}

	private HttpURLConnection getAuthorizedConnection(String urlStr, HttpServletRequest request) throws Exception {
		String cachedAccessToken = redisUtil.get(REDIS_ACCESS_TOKEN_KEY);
		if (cachedAccessToken == null) {
			log.error("CODEF Access Token이 레디스에 없습니다.");
			Common common = Common.builder()
				.srcIp(request.getRemoteAddr())
				.apiMethod(request.getMethod())
				.callApiPath(request.getRequestURI())
				.deviceInfo(request.getHeader("user-agent"))
				.build();
			throw new CustomException(CODEF_TOKEN_NOT_FOUND, LogLevel.ERROR, null, common);
		}

		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		buildCommonHeaders(conn);
		conn.setRequestProperty("Authorization", "Bearer " + cachedAccessToken);
		conn.setDoOutput(true);
		return conn;
	}

	private void buildCommonHeaders(HttpURLConnection conn) {
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
	}

	private String readResponse(HttpURLConnection conn) throws Exception {
		try (BufferedReader br = new BufferedReader(
			new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				response.append(line.trim());
			}
			return URLDecoder.decode(response.toString(), StandardCharsets.UTF_8);
		}
	}

	private String readErrorResponse(HttpURLConnection conn) throws Exception {
		try (BufferedReader br = new BufferedReader(
			new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
			StringBuilder errorResponse = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				errorResponse.append(line.trim());
			}
			return errorResponse.toString();
		}
	}

	private String encryptRSA(String plainText, String base64PublicKey) throws Exception {
		byte[] bytePublicKey = Base64.getDecoder().decode(base64PublicKey);
		KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
		PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(bytePublicKey));

		Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] bytePlain = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(bytePlain);
	}

	private String Encryption(String password) throws Exception {
		return encryptRSA(password, publicKey);
	}
}
