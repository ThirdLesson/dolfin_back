package org.scoula.domain.financialproduct.financialcompany.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.financialcompany.dto.FinancialCompanyDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.scoula.domain.financialproduct.errorCode.FinancialErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class FinancialCompanyServiceImpl implements FinancialCompanyService {

    @Value("${financial_api_base_url}")
    private String apiBaseUrl;

    @Value("${financial_api_auth}")
    private String apiKey;

    private final FinancialCompanyMapper financialCompanyMapper;

    @Override
    public FinancialCompanyDTO getById(Long financialCompanyId) {
        log.debug("금융회사 조회 시작 - ID: {}", financialCompanyId);

        if (financialCompanyId == null) {
            throw new CustomException(INVALID_FINANCIAL_COMPANY_DATA, LogLevel.WARNING,null,null);
        }

        FinancialCompany entity = financialCompanyMapper.selectFinancialCompanyById(financialCompanyId);

        if (entity == null) {
            log.warn("금융회사를 찾을 수 없습니다 - ID: {}", financialCompanyId);
            throw new CustomException(FINANCIAL_COMPANY_NOT_FOUND, LogLevel.WARNING,null,null);
        }

        return FinancialCompanyDTO.fromEntity(entity);
    }

    @Override
    public List<FinancialCompanyDTO> getAll() {
        log.debug("모든 금융회사 목록 조회 시작");

        List<FinancialCompany> entities = financialCompanyMapper.selectAllFinancialCompany();

        List<FinancialCompanyDTO> result = entities.stream()
                .map(FinancialCompanyDTO::fromEntity)
                .collect(Collectors.toList());

        log.info("금융회사 목록 조회 완료 - 총 {}개", result.size());
        return result;
    }

    @Override
    @Transactional
    public void fetchAndSaveFinancialCompanies() {
        log.info("외부 API로부터 금융회사 정보 수집 시작");

        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = String.format("%s/companySearch.json?auth=%s&topFinGrpNo=020000&pageNo=1",
                apiBaseUrl, apiKey);

        // RestClientException만 따로 처리하고 싶을 때만 try-catch 사용
        String jsonResponse;
        try {
            jsonResponse = restTemplate.getForObject(apiUrl, String.class);
        } catch (RestClientException e) {
            log.error("외부 API 호출 실패", e);
            throw new CustomException(DEPOSIT_SAVING_API_CALL_FAILED, LogLevel.ERROR,null,null);
        }

        // 나머지는 try-catch 없이 직접 검증
        if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
            throw new CustomException(DEPOSIT_SAVING_API_CALL_FAILED, LogLevel.ERROR,null,null);
        }

        List<FinancialCompany> companies = parseFinancialCompaniesFromJson(jsonResponse);

        if (companies.isEmpty()) {
            log.warn("처리할 금융회사 데이터가 없습니다");
            return;
        }

        int savedCount = financialCompanyMapper.insertFinancialCompanyBatch(companies);
        log.info("금융회사 데이터 저장 완료 - 새로 저장: {}개", savedCount);
    }

//    JSON 응답을 파싱하여 FinancialCompany 엔티티 리스트로 변환
    private List<FinancialCompany> parseFinancialCompaniesFromJson(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode;

        // JSON 파싱 오류만 따로 처리
        try {
            rootNode = objectMapper.readTree(jsonResponse);
        } catch (Exception e) {
            log.error("JSON 파싱 실패", e);
            throw new CustomException(API_RESPONSE_PARSING_FAILED, LogLevel.ERROR,null,null);
        }

        // 나머지는 try-catch 없이 직접 검증
        JsonNode resultNode = rootNode.get("result");
        if (resultNode == null) {
            throw new CustomException(API_RESPONSE_PARSING_FAILED, LogLevel.ERROR,null,null);
        }

        JsonNode baseListNode = resultNode.get("baseList");
        if (baseListNode == null || !baseListNode.isArray()) {
            throw new CustomException(API_RESPONSE_PARSING_FAILED, LogLevel.ERROR,null,null);
        }

        List<FinancialCompany> companies = new ArrayList<>();

        for (JsonNode companyNode : baseListNode) {
            FinancialCompany company = parseCompanyNode(companyNode);
            if (company != null) {
                companies.add(company);
            }
        }

        log.info("{}개의 금융회사 데이터 파싱 완료", companies.size());
        return companies;
    }

//    개별 회사 노드를 파싱하여 FinancialCompany 엔티티로 변환
    private FinancialCompany parseCompanyNode(JsonNode companyNode) {
        JsonNode codeNode = companyNode.get("fin_co_no");
        JsonNode nameNode = companyNode.get("kor_co_nm");

        if (codeNode == null || nameNode == null) {
            log.debug("필수 필드가 누락된 회사 데이터: {}", companyNode.toString());
            return null;
        }

        String code = codeNode.asText();
        String name = nameNode.asText();

        if (code == null || code.trim().isEmpty() ||
                name == null || name.trim().isEmpty()) {
            log.debug("빈 값이 포함된 회사 데이터 - 코드: {}, 이름: {}", code, name);
            return null;
        }

        FinancialCompanyDTO dto = FinancialCompanyDTO.builder()
                .fin_co_no(code.trim())
                .kor_co_nm(name.trim())
                .build();

        return dto.toEntity();
    }
}
