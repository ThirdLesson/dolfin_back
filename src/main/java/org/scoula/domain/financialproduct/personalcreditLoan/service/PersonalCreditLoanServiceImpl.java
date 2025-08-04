package org.scoula.domain.financialproduct.personalcreditloan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.exception.FinancialErrorCode;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.scoula.domain.financialproduct.personalcreditloan.dto.PersonalCreditLoanDTO;
import org.scoula.domain.financialproduct.personalcreditloan.entity.PersonalCreditLoan;
import org.scoula.domain.financialproduct.personalcreditloan.mapper.PersonalCreditLoanMapper;
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
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalCreditLoanServiceImpl implements PersonalCreditLoanService {

	private final PersonalCreditLoanMapper personalCreditLoanMapper;
	private final FinancialCompanyMapper financialCompanyMapper;

	@Value("${financial_api_base_url}")
	private String apiBaseUrl;

	@Value("${financial_api_auth}")
	private String apiKey;

	@Override
	@Transactional
	public void createPersonalCreditLoan(PersonalCreditLoanDTO personalCreditLoanDTO) {
		log.info("createPersonalCreditLoan: {}", personalCreditLoanDTO);

		//        중복 체크 (금융회사 + 상품명)
		PersonalCreditLoan existingLoan = personalCreditLoanMapper
			.selectPersonalCreditLoanByFinancialCompanyIdAndName(
				personalCreditLoanDTO.getFinancialCompanyId(),
				personalCreditLoanDTO.getName());
		if (existingLoan != null) {
			log.warn("이미 존재하는 개인신용대출 상품입니다. financialCompanyId: {}, name: {}",
				personalCreditLoanDTO.getFinancialCompanyId(), personalCreditLoanDTO.getName());
			throw new CustomException(FinancialErrorCode.DUPLICATE_PersonalCredit_Loan, LogLevel.ERROR, null, null);
		}

		PersonalCreditLoan entity = personalCreditLoanDTO.toEntity();
		personalCreditLoanMapper.insertPersonalCreditLoan(entity);
	}
	//
	@Override
	public PersonalCreditLoanDTO getPersonalCreditLoanById(Long personalCreditLoanId) {
		log.info("getPersonalCreditLoanById: {}", personalCreditLoanId);

		PersonalCreditLoan entity = personalCreditLoanMapper.selectPersonalCreditLoanById(personalCreditLoanId);
		if (entity == null) {
			throw new CustomException(FinancialErrorCode.PERSONAL_LOAN_NOT_FOUND, LogLevel.ERROR, null, null);
		}
		return PersonalCreditLoanDTO.fromEntity(entity);
	}

	@Override
	public List<PersonalCreditLoanDTO> getAllPersonalCreditLoans() {
		log.info("getAllPersonalCreditLoans");

		List<PersonalCreditLoan> entityList = personalCreditLoanMapper.selectAllPersonalCreditLoans();
		return entityList.stream()
			.map(PersonalCreditLoanDTO::fromEntity)
			.toList();
	}

	@Override
	@Transactional
	public void updatePersonalCreditLoan(Long personalCreditLoanId, PersonalCreditLoanDTO personalCreditLoanDTO) {
		log.info("updatePersonalCreditLoan: {}, {}", personalCreditLoanId, personalCreditLoanDTO);

		PersonalCreditLoan existingEntity = personalCreditLoanMapper.selectPersonalCreditLoanById(personalCreditLoanId);
		if (existingEntity == null) {
			throw new CustomException(FinancialErrorCode.PERSONAL_LOAN_NOT_FOUND, LogLevel.ERROR, null, null);
		}

		// DTO -> Entity로 변환하여 ID설정
		PersonalCreditLoan updatedEntity = PersonalCreditLoan.builder()
			.personalCreditLoanId(personalCreditLoanId)  // PK 설정
			.financialCompanyId(personalCreditLoanDTO.getFinancialCompanyId())
			.name(personalCreditLoanDTO.getName())
			.joinWay(personalCreditLoanDTO.getJoinWay())
			.crdtPrdtTypeNm(personalCreditLoanDTO.getCrdtPrdtTypeNm())
			.cbName(personalCreditLoanDTO.getCbName())
			.crdtGrad1(personalCreditLoanDTO.getCrdtGrad1())
			.crdtGrad4(personalCreditLoanDTO.getCrdtGrad4())
			.crdtGrad5(personalCreditLoanDTO.getCrdtGrad5())
			.crdtGrad6(personalCreditLoanDTO.getCrdtGrad6())
			.crdtGrad10(personalCreditLoanDTO.getCrdtGrad10())
			.crdtGrad11(personalCreditLoanDTO.getCrdtGrad11())
			.crdtGrad12(personalCreditLoanDTO.getCrdtGrad12())
			.crdtGrad13(personalCreditLoanDTO.getCrdtGrad13())
			.crdtGradAvg(personalCreditLoanDTO.getCrdtGradAvg())
			.build();
		personalCreditLoanMapper.updatePersonalCreditLoan(updatedEntity);
	}

	@Override
	public List<PersonalCreditLoanDTO> getPersonalCreditLoansByFinancialCompanyId(Long financialCompanyId) {
		log.info("getPersonalCreditLoansByFinancialCompanyId: {}", financialCompanyId);

		List<PersonalCreditLoan> entityList = personalCreditLoanMapper.selectPersonalCreditLoansByFinancialCompanyId(
			financialCompanyId);
		return entityList.stream()
			.map(PersonalCreditLoanDTO::fromEntity)
			.toList();
	}

	@Override
	public List<PersonalCreditLoanDTO> getPersonalCreditLoansByCrdtPrdtTypeNm(String crdtPrdtTypeNm) {
		log.info("getPersonalCreditLoansByCrdtPrdtTypeNm: {}", crdtPrdtTypeNm);
		List<PersonalCreditLoan> entityList = personalCreditLoanMapper.selectPersonalCreditLoansByCrdtPrdtTypeNm(
			crdtPrdtTypeNm);
		return entityList.stream()
			.map(PersonalCreditLoanDTO::fromEntity)
			.toList();
	}

	@Override
	public List<PersonalCreditLoanDTO> getPersonalCreditLoansOrderByAvgRate() {
		log.info("getPersonalCreditLoansOrderByAvgRate");

		List<PersonalCreditLoan> entityList = personalCreditLoanMapper.selectPersonalCreditLoansOrderByAvgRate();
		return entityList.stream()
			.map(PersonalCreditLoanDTO::fromEntity)
			.toList();
	}

	@Override
	@Transactional
	public void synchronizePersonalCreditLoanData() {
		log.info("개인신용대출 상품 동기화 시작");

		try {

			//            API에서 데이터 조회 및 저장
			int savedCount = processPersonalCreditLoanData();

			log.info("개인신용대출 상품 동기화 완료: {}개", savedCount);

		} catch (Exception e) {
			log.error("개인신용대출 데이터 동기화 실패", e);
			throw new CustomException(FinancialErrorCode.DEPOSIT_SAVING_API_CALL_FAILED, LogLevel.ERROR, null, null);
		}
	}

	@Override
	@Transactional
	public void savePersonalCreditLoansBatch(List<PersonalCreditLoanDTO> personalCreditLoanDtos) {
		log.info("savePersonalCreditLoansBatch: {} 개", personalCreditLoanDtos.size());

		if (personalCreditLoanDtos.isEmpty()) {
			return;
		}

		List<PersonalCreditLoan> entityList = personalCreditLoanDtos.stream()
			.map(PersonalCreditLoanDTO::toEntity)
			.collect(Collectors.toList());

		personalCreditLoanMapper.insertPersonalCreditLoansBatch(entityList);
	}

	private int processPersonalCreditLoanData() {
		// API 호출
		String url = String.format("%s?auth=%s&topFinGrpNo=050000&pageNo=1", apiBaseUrl, apiKey);

		RestTemplate restTemplate = new RestTemplate();
		String jsonResponse = restTemplate.getForObject(url, String.class);

		if (jsonResponse == null) {
			throw new CustomException(FinancialErrorCode.API_RESPONSE_EMPTY, LogLevel.ERROR, null, null);
		}

		// JSON 파싱
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode root = mapper.readTree(jsonResponse);
			JsonNode result = root.path("result");

			// API 에러 체크
			if (!"000".equals(result.path("err_cd").asText())) {
				String errMsg = result.path("err_msg").asText();
				throw new CustomException(FinancialErrorCode.API_RESPONSE_INVALID_FORMAT, LogLevel.ERROR, null, null);
			}

			JsonNode baseList = result.path("baseList");
			JsonNode optionList = result.path("optionList");

			// 금융회사 매핑 (finCoNo → financialCompanyId)
			List<FinancialCompany> companies = financialCompanyMapper.selectAllFinancialCompany();
			Map<String, Long> companyMap = new HashMap<>();
			for (FinancialCompany company : companies) {
				companyMap.put(company.getCode(), company.getFinancialCompanyId());
			}

			// 데이터 변환 및 저장
			List<PersonalCreditLoan> entities = new ArrayList<>();

			for (JsonNode baseNode : baseList) {
				String finCoNo = baseNode.path("fin_co_no").asText();
				String finPrdtCd = baseNode.path("fin_prdt_cd").asText();

				Long financialCompanyId = companyMap.get(finCoNo);
				if (financialCompanyId == null) {
					log.debug("금융회사를 찾을 수 없음: {}", finCoNo);
					continue;
				}

				// 대출금리 타입 "A"인 옵션 찾기
				JsonNode matchedOption = findLoanRateOption(optionList, finCoNo, finPrdtCd);

				if (matchedOption != null) {
					PersonalCreditLoan entity = createPersonalCreditLoanEntity(baseNode, matchedOption,
						financialCompanyId);
					if (entity != null) {
						entities.add(entity);
					}
				}
			}

			// 배치 저장
			if (!entities.isEmpty()) {
				personalCreditLoanMapper.insertPersonalCreditLoansBatch(entities);
			}

			return entities.size();

		} catch (Exception e) {
			log.error("JSON 파싱 실패", e);
			throw new CustomException(FinancialErrorCode.API_RESPONSE_PARSING_FAILED, LogLevel.ERROR, null, null);
		}
	}

	//    finCoNo를 financialCompanyId로 매핑
	//    실제로는 financial_company 테이블을 조회하여 매핑해야 함
	private Long mapFinCoNoToFinancialCompanyId(String finCoNo) {
		try {
			return Long.parseLong(finCoNo);
		} catch (NumberFormatException e) {
			log.warn("finCoNo를 Long으로 변환할 수 없습니다: {}", finCoNo);
			return null;
		}
	}

	private PersonalCreditLoan createPersonalCreditLoanEntity(JsonNode baseNode, JsonNode optionNode,
		Long financialCompanyId) {
		try {
			PersonalCreditLoanDTO dto = PersonalCreditLoanDTO.builder()
				.financialCompanyId(financialCompanyId)
				.name(baseNode.path("fin_prdt_nm").asText())
				.joinWay(baseNode.path("join_way").asText())
				.crdtPrdtTypeNm(baseNode.path("crdt_prdt_type_nm").asText())
				.cbName(baseNode.path("cb_name").asText())
				.crdtGrad1(parseDecimalSafe(optionNode.path("crdt_grad_1").asText()))
				.crdtGrad4(parseDecimalSafe(optionNode.path("crdt_grad_4").asText()))
				.crdtGrad5(parseDecimalSafe(optionNode.path("crdt_grad_5").asText()))
				.crdtGrad6(parseDecimalSafe(optionNode.path("crdt_grad_6").asText()))
				.crdtGrad10(parseDecimalSafe(optionNode.path("crdt_grad_10").asText()))
				.crdtGrad11(parseDecimalSafe(optionNode.path("crdt_grad_11").asText()))
				.crdtGrad12(parseDecimalSafe(optionNode.path("crdt_grad_12").asText()))
				.crdtGrad13(parseDecimalSafe(optionNode.path("crdt_grad_13").asText()))
				.crdtGradAvg(parseDecimalSafe(optionNode.path("crdt_grad_avg").asText()))
				.build();

			return dto.toEntity();

		} catch (Exception e) {
			log.debug("엔티티 생성 실패", e);
			return null;
		}
	}

	private BigDecimal parseDecimalSafe(String text) {
		try {
			return (text == null || text.isEmpty() || "null".equals(text)) ? null : new BigDecimal(text);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private JsonNode findLoanRateOption(JsonNode optionList, String finCoNo, String finPrdtCd) {
		for (JsonNode option : optionList) {
			if (finCoNo.equals(option.path("fin_co_no").asText()) &&
				finPrdtCd.equals(option.path("fin_prdt_cd").asText()) &&
				"A".equals(option.path("crdt_lend_rate_type").asText())) {
				return option;
			}
		}
		return null;
	}
}
