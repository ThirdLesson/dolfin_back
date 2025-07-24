package org.scoula.domain.financialproduct.jeonseloan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.errorCode.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;
import org.scoula.domain.financialproduct.jeonseloan.mapper.JeonseLoanMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class JeonseLoanServiceImpl implements JeonseLoanService {

    @Value("${financial_api_base_url}")
    private String apiBaseUrl;

    @Value("${financial_api_auth}")
    private String apiKey;

    private final JeonseLoanMapper jeonseLoanMapper;
    private final FinancialCompanyMapper financialCompanyMapper;


    //    등록
    @Override
    @Transactional
    public void createJeonseLoan(JeonseLoanDTO jeonseLoanDTO) {
        jeonseLoanMapper.insertJeonseLoan(jeonseLoanDTO.toEntity());
    }

    // 배치 저장 (api호출 받아 한번에 저장)
    @Override
    @Transactional
    public void createJeonseLoans(List<JeonseLoanDTO> jeonseLoanDTOs) {
        if (jeonseLoanDTOs == null || jeonseLoanDTOs.isEmpty()) {
            throw new CustomException(FinancialErrorCode.EMPTY_JEONSE_LOAN_LIST, LogLevel.INFO, null, null);
        }
        try {
            List<JeonseLoan> entities = jeonseLoanDTOs.stream()
                    .map(JeonseLoanDTO::toEntity)
                    .toList();

            jeonseLoanMapper.insertJeonseLoans(entities);
            log.info("{}개의 전세대출 데이터를 배치 저장했습니다.", entities.size());
        } catch (Exception e) {
            log.error("전세대출 배치 저장 실패", e);
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_BATCH_SAVE_FAILED, LogLevel.ERROR, null
                    , null);
        }
    }

    //    단건 조회
    @Override
    public JeonseLoanDTO getJeonseLoanById(Long jeonseLoanId) {
        JeonseLoan jeonseLoan = jeonseLoanMapper.selectJeonseLoanById(jeonseLoanId);
        if (jeonseLoan == null) {
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_NOT_FOUND, LogLevel.ERROR, null, null);
        }
        return JeonseLoanDTO.fromEntity(jeonseLoan);
    }

    //    전체 조회
    @Override
    public List<JeonseLoanDTO> getAllJeonseLoans() {
        List<JeonseLoan> entities = jeonseLoanMapper.selectAllJeonseLoans();
        return entities.stream()
                .map(JeonseLoanDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public void updateJeonseLoan(Long jeonseLoanId, JeonseLoanDTO jeonseLoanDTO) {
        // 존재 여부 확인
        JeonseLoan existingLoan = jeonseLoanMapper.selectJeonseLoanById(jeonseLoanId);
        if (existingLoan == null) {
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_NOT_FOUND, LogLevel.ERROR, null, null
            );
        }

        try {
            // ID 설정 후 업데이트
            JeonseLoanDTO updateDTO = JeonseLoanDTO.builder()
                    .jeonseLoanId(jeonseLoanId)  // ID 설정
                    .financialCompanyId(jeonseLoanDTO.getFinancialCompanyId())
                    .name(jeonseLoanDTO.getName())
                    .joinWay(jeonseLoanDTO.getJoinWay())
                    .loanExpensive(jeonseLoanDTO.getLoanExpensive())
                    .erlyFee(jeonseLoanDTO.getErlyFee())
                    .dlyRate(jeonseLoanDTO.getDlyRate())
                    .loanLmt(jeonseLoanDTO.getLoanLmt())
                    .jeonseId(jeonseLoanDTO.getJeonseId())
                    .repayTypeName(jeonseLoanDTO.getRepayTypeName())
                    .lendRateTypeName(jeonseLoanDTO.getLendRateTypeName())
                    .lendRateMin(jeonseLoanDTO.getLendRateMin())
                    .lendRateMax(jeonseLoanDTO.getLendRateMax())
                    .lendRateAvg(jeonseLoanDTO.getLendRateAvg())
                    .build();

            jeonseLoanMapper.updateJeonseLoan(updateDTO.toEntity());
        } catch (Exception e) {
            log.error("전세대출 업데이트 실패: ID={}", jeonseLoanId, e);
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_UPDATE_FAILED, LogLevel.ERROR, null, null);
        }
    }

    // 금융회사별 전세대출 조회
    @Override
    public List<JeonseLoanDTO> getJeonseLoansByFinancialCompanyId(Long financialCompanyId) {
        List<JeonseLoan> entities = jeonseLoanMapper.selectJeonseLoansByFinancialCompanyId(financialCompanyId);
        return entities.stream()
                .map(JeonseLoanDTO::fromEntity)
                .toList();
    }

    // 금리 범위로 검색
    @Override
    public List<JeonseLoanDTO> getJeonseLoansByInterestRateRange(Double minRate, Double maxRate) {
        List<JeonseLoan> entities = jeonseLoanMapper.selectJeonseLoansByInterestRateRange(minRate, maxRate);
        return entities.stream()
                .map(JeonseLoanDTO::fromEntity)
                .toList();
    }

    // 상환 유형별 조회
    @Override
    public List<JeonseLoanDTO> getJeonseLoansByRepayType(String repayTypeName) {
        List<JeonseLoan> entities = jeonseLoanMapper.selectJeonseLoansByRepayType(repayTypeName);
        return entities.stream()
                .map(JeonseLoanDTO::fromEntity)
                .toList();
    }

    // === API 연동 ===

    @Override
    @Transactional
    public void fetchAndSaveJeonseLoansFromApi() {
        try {
            log.info("=== API 호출 시작 ===");

            // 1. URL 확인
            String url = buildApiUrl(null, 1);
            log.info("API URL: {}", url);

            // 2. API 응답 확인
            JsonNode response = callApiAndParseJson(url);
            log.info("API 응답 받음: {}", response != null ? "성공" : "실패");

            if (response != null) {
                log.info("응답 구조 확인:");
                log.info("- has result: {}", response.has("result"));

                if (response.has("result")) {
                    JsonNode result = response.get("result");
                    log.info("- result baseList exists: {}", result.has("baseList"));
                    log.info("- result optionList exists: {}", result.has("optionList"));

                    if (result.has("baseList")) {
                        JsonNode baseList = result.get("baseList");
                        log.info("- baseList size: {}", baseList.size());
                    }
                }
            }

            // 3. DTO 변환 확인
            List<JeonseLoanDTO> dtos = convertJsonToDtos(response);
            log.info("변환된 DTO 개수: {}", dtos.size());

            if (!dtos.isEmpty()) {
                log.info("첫 번째 DTO: {}", dtos.get(0).getName());
            }

            // 4. 저장 시도
            createJeonseLoans(dtos);
            log.info("API에서 {}개의 전세대출 데이터를 성공적으로 저장했습니다.", dtos.size());

        } catch (Exception e) {
            log.error("API에서 전세대출 데이터 가져오기 실패", e);
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_API_CALL_FAILED, LogLevel.ERROR, null, null);
        }
    }

    @Override
    @Transactional
    public void fetchAndSaveJeonseLoansFromApi(Long financialCompanyId) {
        try {
            // 금융회사 코드 조회
            String companyCode = financialCompanyMapper.findCodeById(financialCompanyId);
            if (companyCode == null) {
                throw new CustomException(FinancialErrorCode.FINANCIAL_COMPANY_NOT_FOUND, LogLevel.ERROR, null, null);
            }

            String url = buildApiUrl(companyCode, 1);
            JsonNode response = callApiAndParseJson(url);

            List<JeonseLoanDTO> dtos = convertJsonToDtos(response);
            createJeonseLoans(dtos);

            log.info("금융회사 ID {} - API에서 {}개의 전세대출 데이터를 저장했습니다.",
                    financialCompanyId, dtos.size());
        } catch (Exception e) {
            log.error("특정 금융회사 전세대출 데이터 가져오기 실패: {}", financialCompanyId, e);
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_API_CALL_FAILED, LogLevel.ERROR, null, null);
        }
    }

    @Override
    @Transactional
    public void synchronizeJeonseLoansFromApi() {
        try {
            String url = buildApiUrl(null, 1);
            JsonNode response = callApiAndParseJson(url);

            List<JeonseLoanDTO> apiDtos = convertJsonToDtos(response);

            for (JeonseLoanDTO dto : apiDtos) {
                boolean exists = jeonseLoanMapper.existsByFinancialCompanyIdAndName(
                        dto.getFinancialCompanyId(), dto.getName());

                if (exists) {
                    log.debug("기존 상품 업데이트: {}", dto.getName());
                    // 업데이트 로직은 필요에 따라 구현
                } else {
                    createJeonseLoan(dto);
                    log.debug("새 상품 추가: {}", dto.getName());
                }
            }

            log.info("전세대출 데이터 동기화 완료: {}개 처리", apiDtos.size());
        } catch (Exception e) {
            log.error("전세대출 데이터 동기화 실패", e);
            throw new CustomException(FinancialErrorCode.JEONSE_LOAN_API_CALL_FAILED, LogLevel.INFO, null, null);
        }
    }

    // === 내부 헬퍼 메서드 ===

    private String buildApiUrl(String topFinGrpNo, int pageNo) {
        StringBuilder url = new StringBuilder(apiBaseUrl)
                .append("/rentHouseLoanProductsSearch.json")
                .append("?auth=").append(apiKey)
                .append("&pageNo=").append(pageNo);

        // topFinGrpNo가 null인 경우 기본값 설정
        if (topFinGrpNo != null) {
            url.append("&topFinGrpNo=").append(topFinGrpNo);
        } else {
            // 모든 금융권 조회를 위한 기본값들
            // 020000: 은행, 030200: 여신전문금융회사, 030300: 보험회사, 050000: 기타
            url.append("&topFinGrpNo=020000"); // 은행부터 시작
        }

        return url.toString();
    }

    private JsonNode callApiAndParseJson(String url) {
        try {
            log.info("API 호출 중: {}", url);

            RestTemplate restTemplate = new RestTemplate();
            ObjectMapper objectMapper = new ObjectMapper();

            // HTTP 응답 확인
            String json = restTemplate.getForObject(url, String.class);
            log.info("HTTP 응답 길이: {}", json != null ? json.length() : 0);
            log.info("응답 내용 일부: {}", json != null ? json.substring(0, Math.min(200, json.length())) : "null");

            if (json == null || json.trim().isEmpty()) {
                throw new CustomException(FinancialErrorCode.API_RESPONSE_EMPTY, LogLevel.ERROR, null, null);
            }

            JsonNode parsed = objectMapper.readTree(json);
            log.info("JSON 파싱 성공");

            return parsed;
        } catch (Exception e) {
            log.error("API 호출 또는 파싱 실패: {}", url, e);
            throw new CustomException(FinancialErrorCode.API_RESPONSE_PARSING_FAILED, LogLevel.ERROR, null, null);
        }
    }

    private List<JeonseLoanDTO> convertJsonToDtos(JsonNode response) {
        log.info("=== DTO 변환 시작 ===");

        if (response == null || !response.has("result")) {
            log.warn("응답이 null이거나 result가 없음");
            return new ArrayList<>();
        }

        JsonNode result = response.get("result");
        JsonNode baseList = result.get("baseList");
        JsonNode optionList = result.get("optionList");

        if (baseList == null || !baseList.isArray() || baseList.size() == 0) {
            log.warn("baseList가 null이거나 비어있음");
            return new ArrayList<>();
        }

        log.info("baseList 아이템 수: {}", baseList.size());

        // OptionItem을 Map으로 변환
        Map<String, List<JsonNode>> optionMap = new HashMap<>();
        if (optionList != null && optionList.isArray()) {
            log.info("optionList 아이템 수: {}", optionList.size());
            for (JsonNode option : optionList) {
                String finPrdtCd = option.path("fin_prdt_cd").asText();
                optionMap.computeIfAbsent(finPrdtCd, k -> new ArrayList<>()).add(option);
            }
        }

        List<JeonseLoanDTO> result_list = new ArrayList<>();

        for (JsonNode baseItem : baseList) {
            String finPrdtCd = baseItem.path("fin_prdt_cd").asText();
            String finCoNo = baseItem.path("fin_co_no").asText();

            log.debug("처리 중인 상품: {} (회사코드: {})", finPrdtCd, finCoNo);

            // 금융회사 코드 → ID 매핑
            Long financialCompanyId = financialCompanyMapper.findIdByCode(finCoNo);
            if (financialCompanyId == null) {
                log.warn("해당 코드의 금융회사 ID 없음: {}", finCoNo);
                continue;
            }

            List<JsonNode> options = optionMap.getOrDefault(finPrdtCd, new ArrayList<>());

            if (options.isEmpty()) {
                result_list.add(createDtoFromJson(baseItem, null, financialCompanyId));
            } else {
                for (JsonNode option : options) {
                    result_list.add(createDtoFromJson(baseItem, option, financialCompanyId));
                }
            }
        }

        log.info("최종 변환된 DTO 수: {}", result_list.size());
        return result_list;
    }

    private JeonseLoanDTO createDtoFromJson(JsonNode base, JsonNode option, Long financialCompanyId) {
        return JeonseLoanDTO.builder()
                .financialCompanyId(financialCompanyId)
                .name(base.path("fin_prdt_nm").asText())
                .joinWay(base.path("join_way").asText())
                .loanExpensive(base.path("loan_inci_expn").asText())
                .erlyFee(base.path("erly_rpay_fee").asText())
                .dlyRate(parseDecimalSafe(base.path("dly_rate").asText()))
                .loanLmt(base.path("loan_lmt").asText())
                .repayTypeName(option != null ? option.path("rpay_type_nm").asText() : null)
                .lendRateTypeName(option != null ? option.path("lend_rate_type_nm").asText() : null)
                .lendRateMin(option != null ? parseDecimalSafe(option.path("lend_rate_min").asText()) : null)
                .lendRateMax(option != null ? parseDecimalSafe(option.path("lend_rate_max").asText()) : null)
                .lendRateAvg(option != null ? parseDecimalSafe(option.path("lend_rate_avg").asText()) : null)
                .build();
    }

    private BigDecimal parseDecimalSafe(String value) {
        try {
            if (value == null || value.trim().isEmpty() || "null".equals(value)) {
                return null;
            }
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            log.warn("BigDecimal 파싱 실패: {}", value);
            return null;
        }
    }
}
