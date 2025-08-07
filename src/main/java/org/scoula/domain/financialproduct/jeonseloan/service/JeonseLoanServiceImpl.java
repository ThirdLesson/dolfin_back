package org.scoula.domain.financialproduct.jeonseloan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import springfox.documentation.spring.web.plugins.Docket;

import org.scoula.domain.financialproduct.constants.JeonseLoanInterestFilterType;
import org.scoula.domain.financialproduct.exception.FinancialErrorCode;
import org.scoula.domain.financialproduct.jeonseloan.dto.JeonseLoanDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;
import org.scoula.domain.financialproduct.jeonseloan.mapper.JeonseLoanMapper;
import org.scoula.global.exception.CustomException;
import org.scoula.global.kafka.dto.LogLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class JeonseLoanServiceImpl implements JeonseLoanService {

	private final JeonseLoanMapper jeonseLoanMapper;

	@Override
	public List<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanInterestFilterType filterType) {
		log.info("전세대출상품 리스트 전체 조회");
		List<JeonseLoanResponseDTO> response = jeonseLoanMapper.getAllJeonseLoans(filterType);
		log.info("조회 된 전세 대출 리스트 수  : {}", response.size());
		return response;
	}
}
