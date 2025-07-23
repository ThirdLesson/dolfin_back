package org.scoula.domain.financialproduct.depositsaving.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.depositsaving.dto.DepositSavingDTO;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSaving;
import org.scoula.domain.financialproduct.depositsaving.entity.DepositSavingType;
import org.scoula.domain.financialproduct.depositsaving.mapper.DepositSavingMapper;
import org.scoula.domain.financialproduct.errorCode.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositSavingServiceImpl implements DepositSavingService {


    @Value("${financial_api_base_url}")
    private String apiBaseUrl;

    @Value("${financial_api_auth}")
    private String apiKey;

    private final DepositSavingMapper depositSavingMapper;
    private final FinancialCompanyMapper financialCompanyMapper;

    //  등록
    @Override
    @Transactional
    public void createDepositSaving(DepositSavingDTO depositSavingDTO) {
        DepositSaving entity = depositSavingDTO.toEntity();
        depositSavingMapper.insertDepositSaving(entity);
    }

    //    단건 조회
    @Override
    public DepositSavingDTO getDepositSavingById(Long depositSavingId) {
        DepositSaving entity = depositSavingMapper.selectDepositSavingById(depositSavingId);
        if (entity == null) {
            throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_API_CALL_FAILED, LogLevel.ERROR, null, null);
        }
        return DepositSavingDTO.fromEntity(entity);
    }

    // 전체 조회
    @Override
    public List<DepositSavingDTO> getAllDepositSavings() {
        return depositSavingMapper.selectAllDepositSavings()
                .stream()
                .map(DepositSavingDTO::fromEntity)
                .toList();
    }

    //    수정
    @Override
    @Transactional
    public void updateDepositSaving(DepositSavingDTO depositSavingDTO) {
        DepositSaving entity = depositSavingDTO.toEntity();
        int update = depositSavingMapper.updateDepositSaving(entity);
        if (update == 0) {
            throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_API_CALL_FAILED,
                    LogLevel.ERROR, null, null);
        }
    }

    //    삭제
    @Override
    @Transactional
    public void deleteDepositSaving(Long depositSavingId) {
        depositSavingMapper.deleteDepositSaving(depositSavingId);
    }

    //    타입별 조회
    @Override
    public List<DepositSaving> getDepositSavingsByType(String type) {
        DepositSavingType depositSavingType;
        try {
            depositSavingType = DepositSavingType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new CustomException(FinancialErrorCode.INVALID_FINANCIAL_COMPANY_DATA, LogLevel.ERROR, null, null);
        }
        return depositSavingMapper.selectByType(depositSavingType);
    }

    //    금융회사별 조회
    @Override
    public List<DepositSaving> getDepositSavingsByFinancialCompany(Long financialCompanyId) {
        return depositSavingMapper.selectByFinancialCompany(financialCompanyId);
    }

    //    금리 범위 조회
    @Override
    public List<DepositSaving> getDepositSavingsByInterestRateRange(double minRate, double maxRate) {
        BigDecimal minRateBd = BigDecimal.valueOf(minRate);
        BigDecimal maxRateBd = BigDecimal.valueOf(maxRate);
        return depositSavingMapper.selectByInterestRateRange(minRateBd, maxRateBd);
    }


//API 호출 및 저장

    @Override
    public void fetchAndSaveDepositSaving() throws JsonProcessingException {
        log.info("예금 상품 동기화 시작");

        String[] groups = {"020000", "030000", "050000"};
        int total = 0;

        for (String group : groups) {
            try {
                int saved = processDepositGroup(group);
                total += saved;
                log.info("{} 예금 {}개 저장", getGroupName(group), saved);
            } catch (Exception e) {
                log.error("{} 예금 실패: {}", getGroupName(group), e.getMessage());
            }
        }

        log.info("예금 동기화 완료: {}개", total);
    }

    @Override
    public void fetchAndSaveSavingProducts() {
        log.info("적금 상품 동기화 시작");


        String[] groups = {"020000"};

        int total = 0;

        for (String group : groups) {
            try {
                log.info("적금 그룹 {} 처리 시작", getGroupName(group));
                int saved = processSavingGroup(group);
                total += saved;
                log.info("{} 적금 {}개 저장 완료", getGroupName(group), saved);
            } catch (Exception e) {
                log.error("{} 적금 처리 실패: {}", getGroupName(group), e.getMessage(), e);
            }
        }

        log.info("적금 동기화 완료: 총 {}개", total);
    }

    private int processDepositGroup(String groupCode) throws JsonProcessingException {
        // 예금 API 호출
        String url = String.format("%s/depositProductsSearch.json?auth=%s&topFinGrpNo=%s&pageNo=1",
                apiBaseUrl, apiKey, groupCode);

        log.info("예금 API 호출: {}", url);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        String json = restTemplate.getForObject(url, String.class);

        return processApiResponse(json, DepositSavingType.DEPOSIT);
    }

    private int processSavingGroup(String groupCode) {
        String url = String.format("%s/savingProductsSearch.json?auth=%s&topFinGrpNo=%s&pageNo=1",
                apiBaseUrl, apiKey, groupCode);

        log.info("적금 API 호출 URL: {}", url);

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            String json = restTemplate.getForObject(url, String.class);

            log.info("적금 API 응답 받음 - 길이: {}", json != null ? json.length() : 0);
            log.info("적금 API 응답 첫 200자: {}", json != null && json.length() > 200 ? json.substring(0, 200) : json);

            return processApiResponse(json, DepositSavingType.SAVING);

        } catch (Exception e) {
            log.error("적금 API 호출 실패 - 그룹: {}, URL: {}, 오류: {}", groupCode, url, e.getMessage(), e);
            throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_API_CALL_FAILED,
                    LogLevel.ERROR, null, null);
        }
    }

    private int processApiResponse(String json, DepositSavingType type) throws JsonProcessingException {
        // JSON 파싱
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        JsonNode baseList = root.path("result").path("baseList");
        JsonNode optionList = root.path("result").path("optionList");

        log.info("API 응답 - baseList: {}개, optionList: {}개",
                baseList.size(), optionList.size());

        // 금융회사 매핑
        List<FinancialCompany> companies = financialCompanyMapper.selectAllFinancialCompany();
        Map<String, FinancialCompany> companyMap = new HashMap<>();
        for (FinancialCompany company : companies) {
            companyMap.put(company.getCode(), company);
        }

        // 데이터 변환 및 저장
        List<DepositSaving> entities = new ArrayList<>();

        for (JsonNode base : baseList) {
            String finCoNo = base.path("fin_co_no").asText();
            String finPrdtCd = base.path("fin_prdt_cd").asText();

            FinancialCompany company = companyMap.get(finCoNo);
            if (company == null) {
                log.warn("금융회사 매핑 실패: {}", finCoNo);
                continue;
            }

            // 옵션 찾기
            List<JsonNode> options = StreamSupport.stream(optionList.spliterator(), false)
                    .filter(opt -> finCoNo.equals(opt.path("fin_co_no").asText()) &&
                            finPrdtCd.equals(opt.path("fin_prdt_cd").asText()))
                    .toList();

            if (options.isEmpty()) {
                entities.add(createEntity(base, null, company, type));
            } else {
                for (JsonNode option : options) {
                    entities.add(createEntity(base, option, company, type));
                }
            }
        }

        // 배치 저장
        if (!entities.isEmpty()) {
            depositSavingMapper.insertBatch(entities);
            log.info("{}개 상품 저장 완료", entities.size());
        }

        return entities.size();
    }


    private DepositSaving createEntity(JsonNode base, JsonNode option, FinancialCompany company, DepositSavingType type) {
        DepositSavingDTO dto = DepositSavingDTO.builder()
                .financialCompanyId(company.getFinancialCompanyId())
                .type(type)
                .name(base.path("fin_prdt_nm").asText())
                .joinWay(base.path("join_way").asText())
                .interestDescription(truncateString(base.path("mtrt_int").asText(), 1000))
                .spclCondition(truncateString(base.path("spcl_cnd").asText(), 1000))  // ← 이게 핵심!
                .ctcNote(truncateString(base.path("etc_note").asText(), 1000))
                .maxLimit(base.path("max_limit").asText())
                .saveMonth(option != null ? parseIntSafe(option.path("save_trm").asText()) : null)
                .interestRate(option != null ? parseDecimalSafe(option.path("intr_rate").asText()) : null)
                .maxInterestRate(option != null ? parseDecimalSafe(option.path("intr_rate2").asText()) : null)
                .interestRateType(option != null ? option.path("intr_rate_type_nm").asText() : null)
                .reserveType(type == DepositSavingType.DEPOSIT ? "정기예금" : "정기적금")
                .build();

        return dto.toEntity();
    }

    private String truncateString(String str, int maxLength) {
        if (str == null || str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength);
    }

    private String getGroupName(String code) {
        return switch (code) {
            case "020000" -> "은행";
            case "030000" -> "카드사";
            case "050000" -> "저축은행";
            default -> code;
        };
    }

    private Integer parseIntSafe(String text) {
        try {
            return text == null || text.isEmpty() ? null : Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal parseDecimalSafe(String text) {
        try {
            return text == null || text.isEmpty() ? null : new BigDecimal(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}


