package org.scoula.domain.financialproduct.financialcompany.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.financialcompany.dto.request.FinancialCompanyRequestDTO;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.global.exception.CustomException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.scoula.domain.financialproduct.errorCode.FinancialErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class FinancialCompanyServiceImpl implements FinancialCompanyService {

	private final FinancialCompanyMapper financialCompanyMapper;
	private final RestTemplate restTemplate;

	@Value("${financial.company.api_url}")
	private String API_URL;
	// 금감원 -> 네이버 이용시 홈페이지와 전화번호가 제공되지 않아 이용
	@Value("${fss.company.api.url}")
	private String FSS_API_URL;

	@Value("${fss.company.api.key}")
	private String FSS_API_KEY;
	// 부분 매칭
	private static final Map<String, String> BANK_NAME_MAPPING = Map.of(
		"NH농협", "농협은행주식회사",
		"IBK기업", "중소기업은행",
		"KDB산업", "한국산업은행",
		"SC제일", "한국스탠다드차타드은행",
		"iM뱅크", "아이엠뱅크"
	);

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

		// 1. 네이버 API 호출
		ResponseEntity<String> naverResponse = callNaverApi();

		// 2. 금감원 API 호출
		Map<String, FssCompanyData> fssDataMap = fetchFssCompanyData();

		// 3. 네이버 JSON 파싱하여 금융회사 데이터 추출
		List<FinancialCompanyRequestDTO> companyDTOs = parseNaverPayResponse(naverResponse.getBody(), fssDataMap);

		if (companyDTOs.isEmpty()) {
			log.warn("외부 API에서 가져온 금융회사 데이터가 없습니다.");
			return List.of();
		}

		// 4. 중복 체크 후 저장할 데이터만 필터링
		List<FinancialCompany> newCompanies = companyDTOs.stream()
			.filter(dto -> !isExistByCode(dto.getFinCoNo())) // 중복체크
			.map(FinancialCompanyRequestDTO::toEntity)
			.toList();

		if (newCompanies.isEmpty()) {
			log.info("저장할 새로운 금융회사가 없습니다(기존 데이터와 중복)");
			return List.of();
		}

		// 5. 배치로 한번에 저장
		int saveCount = financialCompanyMapper.insertBatch(newCompanies);
		log.info("새로운 금융회사 {}개 저장완료", saveCount);

		// 6. 저장된 데이터 responseDTO 변환 후 반환
		return newCompanies.stream()
			.map(FinancialCompanyResponseDTO::fromEntity)
			.toList();
	}

	@Override
	public FinancialCompany getById(Long financialCompanyId) {
		return financialCompanyMapper.selectById(financialCompanyId);
	}

	// 네이버 API 호출
	private ResponseEntity<String> callNaverApi() {
		ResponseEntity<String> response = restTemplate.getForEntity(API_URL, String.class);
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new CustomException(API_RESPONSE_PARSING_FAILED, null, null, null);
		}
		return response;
	}

	// 금감원 API 데이터 조회
	private Map<String, FssCompanyData> fetchFssCompanyData() {
		Map<String, FssCompanyData> fssDataMap = new HashMap<>();

		log.info("금감원 API 호출 시작");

		String requestUrl = FSS_API_URL + "?auth=" + FSS_API_KEY + "&topFinGrpNo=020000&pageNo=1";
		ResponseEntity<String> response =  restTemplate.getForEntity(requestUrl, String.class);

		if (response.getStatusCode() != HttpStatus.OK) {
			log.warn("금감원 API 호출 실패, 전화번호/홈페이지 정보 없이 진행");
			return fssDataMap;
		}

		JsonNode rootNode = parseJsonResponse(response.getBody());
		JsonNode baseList = rootNode.path("result").path("baseList");

		if (baseList.isArray()) {
			for (JsonNode bankNode : baseList) {
				String korCoNm = bankNode.path("kor_co_nm").asText();
				String homepageUrl = bankNode.path("homp_url").asText();
				String callTel = bankNode.path("cal_tel").asText();

				FssCompanyData fssData = FssCompanyData.builder()
					.name(korCoNm)
					.homepageUrl(homepageUrl.isEmpty() ? null : homepageUrl)
					.callNumber(callTel.isEmpty() ? null : formatPhoneNumber(callTel))
					.build();

				fssDataMap.put(korCoNm, fssData);
			}
		}

		log.info("금감원 데이터 {}개 수집 완료", fssDataMap.size());
		return fssDataMap;
	}

	// JSON 응답 파싱
	private JsonNode parseJsonResponse(String jsonData) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(jsonData);
		} catch (Exception e) {
			log.error("JSON 파싱 실패", e);
			throw new CustomException(API_RESPONSE_PARSING_FAILED, null, null, null);
		}
	}

	// 네이버 페이 API 응답 JSON 파싱 (금감원 데이터와 결합)
	private List<FinancialCompanyRequestDTO> parseNaverPayResponse(String jsonData, Map<String, FssCompanyData> fssDataMap) {
		List<FinancialCompanyRequestDTO> companyDTOs = new ArrayList<>();
		Set<String> processedCodes = new HashSet<>();

		JsonNode rootNode = parseJsonResponse(jsonData);
		JsonNode queries = rootNode.path("pageProps")
			.path("dehydratedState")
			.path("queries");

		for (JsonNode query : queries) {
			extractCompaniesFromNode(query.path("state")
					.path("data")
					.path("result")
					.path("companies"),
				companyDTOs, processedCodes, fssDataMap);
		}

		log.info("파싱된 금융회사 수: {}", companyDTOs.size());
		return companyDTOs;
	}

	// 금감원 API 대상이 아닌 기관들
	private static final Set<String> NON_BANK_INSTITUTIONS = Set.of(
		"신협",           // 신용협동조합
		"우정사업본부",    // 정부기관
		"우리투자"        // 증권회사
	);

	// JSON 노드에서 금융회사 데이터 추출 (금감원 데이터와 결합)
	private void extractCompaniesFromNode(JsonNode companies,
		List<FinancialCompanyRequestDTO> companyDTOs,
		Set<String> processedCodes,
		Map<String, FssCompanyData> fssDataMap) {

		if (companies.isArray()) {
			for (JsonNode companyNode : companies) {
				String code = companyNode.path("code").asText();
				String name = companyNode.path("name").asText();

				if (processedCodes.contains(code)) {
					continue;
				}

				// 금감원 대상 외 기관 체크
				if (NON_BANK_INSTITUTIONS.contains(name)) {
					log.info("금감원 대상 외 기관 저장: {}", name);
					FinancialCompanyRequestDTO dto = FinancialCompanyRequestDTO.builder()
						.finCoNo(code)  // 네이버페이 기준 코드
						.name(name)
						.homepageUrl(null)  // 기본 정보만 저장
						.callNumber(null)
						.build();
					companyDTOs.add(dto);
					processedCodes.add(code);
					continue;
				}

				// 금감원 데이터와 매칭 (일반 은행들)
				FssCompanyData fssData = findFssDataByName(name, fssDataMap);

				FinancialCompanyRequestDTO dto = FinancialCompanyRequestDTO.builder()
					.finCoNo(code)  // 네이버페이 기준 코드
					.name(name)
					.homepageUrl(fssData != null ? fssData.getHomepageUrl() : null)
					.callNumber(fssData != null ? fssData.getCallNumber() : null)
					.build();

				companyDTOs.add(dto);
				processedCodes.add(code);

				log.debug("은행 매칭: {} → FSS: {}", name, fssData != null ? fssData.getName() : "매칭 실패");
			}
		}
	}

	// 네이버 은행명으로 금감원 데이터 찾기
	private FssCompanyData findFssDataByName(String naverName, Map<String, FssCompanyData> fssDataMap) {
		// 1. 직접 매칭 시도
		FssCompanyData directMatch = fssDataMap.get(naverName);
		if (directMatch != null) {
			return directMatch;
		}

		// 2. 매핑 테이블 사용
		String mappedName = BANK_NAME_MAPPING.get(naverName);
		if (mappedName != null) {
			FssCompanyData mappedMatch = fssDataMap.get(mappedName);
			if (mappedMatch != null) {
				return mappedMatch;
			}
		}

		// 3. 부분 매칭 시도 (contains)
		for (Map.Entry<String, FssCompanyData> entry : fssDataMap.entrySet()) {
			String fssName = entry.getKey();
			if (fssName.contains(naverName) || naverName.contains(fssName.replace("은행", "").replace("주식회사", "").trim())) {
				return entry.getValue();
			}
		}

		log.warn("금감원 데이터에서 매칭되지 않은 은행: {}", naverName);
		return null;
	}

	// 금융회사 중복 체크
	private boolean isExistByCode(String code) {
		FinancialCompany company = financialCompanyMapper.selectByCode(code);
		return company != null;
	}

	// 전화번호 포맷팅 메서드
	private String formatPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			return phoneNumber;
		}

		// 숫자만 추출
		String digits = phoneNumber.replaceAll("[^0-9]", "");

		if (digits.length() == 8) {
			// 8자리: 1588-5000
			return digits.substring(0, 4) + "-" + digits.substring(4);
		} else if (digits.length() == 10) {
			// 10자리: 02-1234-5678
			return digits.substring(0, 2) + "-" + digits.substring(2, 6) + "-" + digits.substring(6);
		} else if (digits.length() == 11) {
			// 11자리: 010-1234-5678
			return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
		}

		// 그 외의 경우 원본 반환
		return phoneNumber;
	}

	// 내부 클래스: 금감원 데이터 구조
	@Builder
	@Getter
	private static class FssCompanyData {
		private final String name;
		private final String homepageUrl;
		private final String callNumber;
	}

}
