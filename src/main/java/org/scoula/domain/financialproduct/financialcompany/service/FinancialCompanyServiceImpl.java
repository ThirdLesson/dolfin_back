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

import org.scoula.global.kafka.dto.Common;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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

	@Value("${financial.company.api.url}")
	private String API_URL;
	@Value("${fss.company.api.url}")
	private String FSS_API_URL;
	@Value("${fss.api.key}")
	private String FSS_API_KEY;

	private static final Map<String, String> BANK_NAME_MAPPING = Map.of(
		"NH농협", "농협은행주식회사",
		"IBK기업", "중소기업은행",
		"KDB산업", "한국산업은행",
		"SC제일", "한국스탠다드차타드은행",
		"iM뱅크", "아이엠뱅크"
	);

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

	@Override
	@Transactional
	public List<FinancialCompanyResponseDTO> fetchAndSaveFinancialCompanies() {
		log.info("외부 API로부터 금융회사 정보 수집 시작");
		ResponseEntity<String> naverResponse = callNaverApi();
		Map<String, FssCompanyData> fssDataMap = fetchFssCompanyData();
		List<FinancialCompanyRequestDTO> companyDTOs = parseNaverPayResponse(naverResponse.getBody(), fssDataMap);
		if (companyDTOs.isEmpty()) {
			log.warn("외부 API에서 가져온 금융회사 데이터가 없습니다.");
			return List.of();
		}
		List<FinancialCompany> newCompanies = companyDTOs.stream()
			.filter(dto -> !isExistByCode(dto.getFinCoNo())) 
			.map(FinancialCompanyRequestDTO::toEntity)
			.toList();
		if (newCompanies.isEmpty()) {
			log.info("저장할 새로운 금융회사가 없습니다(기존 데이터와 중복)");
			return List.of();
		}
		int saveCount = financialCompanyMapper.insertBatch(newCompanies);
		log.info("새로운 금융회사 {}개 저장완료", saveCount);
		return newCompanies.stream()
			.map(FinancialCompanyResponseDTO::fromEntity)
			.toList();
	}

	@Override
	public FinancialCompany getById(Long financialCompanyId) {
		return financialCompanyMapper.selectById(financialCompanyId);
	}

	private ResponseEntity<String> callNaverApi() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
		headers.add("Referer", "https://new-m.pay.naver.com/");
		headers.add("Accept", "application/json");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.GET, entity, String.class);
			return response;
		} catch (Exception e) {
			System.out.println("네이버 API 호출 실패: " + e.getMessage());
			throw e;
		}
		
	}

	private Map<String, FssCompanyData> fetchFssCompanyData() {
		Map<String, FssCompanyData> fssDataMap = new HashMap<>();

		log.info("금감원 API 호출 시작");

		String requestUrl = FSS_API_URL + "?auth=" + FSS_API_KEY + "&topFinGrpNo=020000&pageNo=1";
		ResponseEntity<String> response =  restTemplate.getForEntity(requestUrl, String.class);
		log.info("금감원 API 응답 코드: {}", response.getStatusCode());
		log.debug("금감원 API 응답 바디: {}", response.getBody());

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

	private JsonNode parseJsonResponse(String jsonData) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.readTree(jsonData);
		} catch (Exception e) {
			log.error("JSON 파싱 실패", e);
			throw new CustomException(API_RESPONSE_PARSING_FAILED, LogLevel.WARNING, null, Common.builder().build(), "금융회사 데이터 파싱 중 에러 발생");
		}
	}

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

	private static final Set<String> NON_BANK_INSTITUTIONS = Set.of(
		"신협",           
		"우정사업본부",    
		"우리투자"        
	);

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

				if (NON_BANK_INSTITUTIONS.contains(name)) {
					log.info("금감원 대상 외 기관 저장: {}", name);
					FinancialCompanyRequestDTO dto = FinancialCompanyRequestDTO.builder()
						.finCoNo(code)  
						.name(name)
						.homepageUrl(null)  
						.callNumber(null)
						.build();
					companyDTOs.add(dto);
					processedCodes.add(code);
					continue;
				}

				FssCompanyData fssData = findFssDataByName(name, fssDataMap);

				FinancialCompanyRequestDTO dto = FinancialCompanyRequestDTO.builder()
					.finCoNo(code) 
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

	private FssCompanyData findFssDataByName(String naverName, Map<String, FssCompanyData> fssDataMap) {
		FssCompanyData directMatch = fssDataMap.get(naverName);
		if (directMatch != null) {
			return directMatch;
		}

		String mappedName = BANK_NAME_MAPPING.get(naverName);
		if (mappedName != null) {
			FssCompanyData mappedMatch = fssDataMap.get(mappedName);
			if (mappedMatch != null) {
				return mappedMatch;
			}
		}

		for (Map.Entry<String, FssCompanyData> entry : fssDataMap.entrySet()) {
			String fssName = entry.getKey();
			if (fssName.contains(naverName) || naverName.contains(fssName.replace("은행", "").replace("주식회사", "").trim())) {
				return entry.getValue();
			}
		}

		log.warn("금감원 데이터에서 매칭되지 않은 은행: {}", naverName);
		return null;
	}

	private boolean isExistByCode(String code) {
		FinancialCompany company = financialCompanyMapper.selectByCode(code);
		return company != null;
	}

	private String formatPhoneNumber(String phoneNumber) {
		if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
			return phoneNumber;
		}

		String digits = phoneNumber.replaceAll("[^0-9]", "");

		if (digits.length() == 8) {
			return digits.substring(0, 4) + "-" + digits.substring(4);
		} else if (digits.length() == 10) {
			return digits.substring(0, 2) + "-" + digits.substring(2, 6) + "-" + digits.substring(6);
		} else if (digits.length() == 11) {
			return digits.substring(0, 3) + "-" + digits.substring(3, 7) + "-" + digits.substring(7);
		}

		return phoneNumber;
	}

	@Builder
	@Getter
	private static class FssCompanyData {
		private final String name;
		private final String homepageUrl;
		private final String callNumber;
	}

}
