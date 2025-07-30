package org.scoula.domain.financialproduct.financialcompany.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.errorCode.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.dto.request.FinancialCompanyRequestDTO;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.global.exception.CustomException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.scoula.domain.financialproduct.errorCode.FinancialErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class FinancialCompanyServiceImpl implements FinancialCompanyService {

	private final FinancialCompanyMapper financialCompanyMapper;
	private final RestTemplate restTemplate;

	private static final String API_URL =
		"https://new-m.pay.naver.com/savings/_next/data/Sxex5f02tUa16uzMlTx45/list/deposit.json?productCategory=deposit";

	// 금융회사 리스트 전체 조회
	@Override
	public List<FinancialCompanyResponseDTO> getAll() {
		log.debug("모든 금융회사 목록 조회 시작");

		List<FinancialCompany> entities = financialCompanyMapper.selectAllFinancialCompany();

		List<FinancialCompanyResponseDTO> result = entities.stream()
			.map(FinancialCompanyResponseDTO::fromEntity)
			.toList();

		log.info("금융회사 목록 조회 완료 - 총 {}개", result.size());
		return result;
	}

	// 금융회사 API 호출 및 저장
	@Override
	@Transactional
	public List<FinancialCompanyResponseDTO> fetchAndSaveFinancialCompanies() {
		log.info("외부 API로부터 금융회사 정보 수집 시작");

		try {
			// 1. API 호출
			ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new CustomException(FinancialErrorCode.API_RESPONSE_PARSING_FAILED, null, null, null);
			}

			// 2. JSON 파싱하여 금융회사 데이터 추출
			List<FinancialCompanyRequestDTO> companyDTOs = parseNaverPayResponse(response.getBody());

			if (companyDTOs.isEmpty()) {
				log.warn("외부 API에서 가져온 금융회사 데이터가 없습니다.");
				return List.of();
			}

			// 3. 중복 체크 후 저장할 데이터만 필터링
			List<FinancialCompany> newCompanies = companyDTOs.stream()
				.filter(dto -> !isExistByCode(dto.getFinCoNo())) // 중복체크
				.map(FinancialCompanyRequestDTO::toEntity)
				.toList();

			if (newCompanies.isEmpty()) {
				log.info("저장할 새로운 금융회사가 없습니다(기존 데이터와 중복)");
				return List.of();
			}

			// 4. 배치로 한번에 저장
			int saveCount = financialCompanyMapper.insertBatch(newCompanies);
			log.info("새로운 금융회사 {}개 저장완료", saveCount);

			// 5. 저장된 데이터 responseDTO 변환 후 반환
			return newCompanies.stream()
				.map(FinancialCompanyResponseDTO::fromEntity)
				.toList();

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			log.error("외부 API 데이터 저장 중 오류 발생", e);
			throw new CustomException(FinancialErrorCode.API_RESPONSE_PARSING_FAILED, null, null, null);
		}
	}

	@Override
	public FinancialCompany getById(Long financialCompanyId) {
		FinancialCompany financialCompany = financialCompanyMapper.selectById(financialCompanyId);
		return financialCompany;
	}

	// 네이버 페이 API 응답 JSON 파싱
	private List<FinancialCompanyRequestDTO> parseNaverPayResponse(String jsonData) {
		List<FinancialCompanyRequestDTO> companyDTOs = new ArrayList<>();
		Set<String> processedCodes = new HashSet<>();

		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(jsonData);

			JsonNode queries = rootNode.path("pageProps")
				.path("dehydratedState")
				.path("queries");

			for (JsonNode query : queries) {
				extractCompaniesFromNode(query.path("state")
						.path("data")
						.path("result")
						.path("companies"),
					companyDTOs, processedCodes);
			}
		} catch (Exception e) {
			log.error("네이버 페이 JSON 파싱 실패", e);
			throw new CustomException(API_RESPONSE_PARSING_FAILED, null, null, null);
		}

		log.info("파싱된 금융회사 수: {}", companyDTOs.size());
		return companyDTOs;
	}

	// JSON 노드에서 금융회사 데이터 추출
	private void extractCompaniesFromNode(JsonNode companies,
		List<FinancialCompanyRequestDTO> companyDTOs,
		Set<String> processedCodes) {
		if (companies.isArray()) {
			for (JsonNode companyNode : companies) {
				String code = companyNode.path("code").asText();
				String name = companyNode.path("name").asText();

				if (processedCodes.contains(code)) {
					continue;
				}

				FinancialCompanyRequestDTO dto = FinancialCompanyRequestDTO.builder()
					.finCoNo(code)
					.name(name)
					.homepageUrl(null)
					.callNumber(null)
					.build();

				companyDTOs.add(dto);
				processedCodes.add(code);
			}
		}
	}

	// 금융회사 중복 체크
	private boolean isExistByCode(String code) {
		try {
			FinancialCompany company = financialCompanyMapper.selectByCode(code);
			return company != null;
		} catch (Exception e) {
			log.error("금융회사 코드 중복 체크 중 오류 발생: {}", code, e);
			return false;
		}
	}
}
// // 전화번호 포맷팅 메서드
// private String formatPhoneNumber(String phoneNumber) {
// 	if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
// 		return phoneNumber;
// 	}
//
// 	// 숫자만 추출
// 	String digits = phoneNumber.replaceAll("[^0-9]", "");
//
// 	if (digits.length() == 8) {
// 		// 8자리: 1522-1000
// 		return digits.substring(0, 4) + "-" + digits.substring(4);
// 	} else if (digits.length() == 10) {
// 		// 10자리: 02-1234-5678
// 		return digits.substring(0, 2) + "-" + digits.substring(2, 6) + "-" + digits.substring(6);
// 	}
//
// 	// 8자리, 10자리가 아닌 경우 원본 반환
// 	return phoneNumber;
// }
