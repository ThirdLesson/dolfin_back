package org.scoula.domain.financialproduct.jeonseloan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.scoula.domain.financialproduct.constants.JeonseLoanRateType;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.service.FinancialCompanyService;
import org.scoula.domain.financialproduct.jeonseloan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanDetailResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialproduct.jeonseloan.entity.JeonseLoan;
import org.scoula.domain.financialproduct.jeonseloan.mapper.JeonseLoanMapper;
import org.scoula.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JeonseLoanServiceImpl implements JeonseLoanService {

	private final JeonseLoanMapper jeonseLoanMapper;
	private final FinancialCompanyService financialCompanyService;

	// 전세대출 전체 상품 조회
	@Override
	public Page<JeonseLoanResponseDTO> getAllJeonseLoans(JeonseLoanRequestDTO request, Pageable pageable) {

		int offset = (int)pageable.getOffset();
		int limit = pageable.getPageSize();

		List<JeonseLoan> jeonseLoans = jeonseLoanMapper.findJeonseLoansWithFilter(request, offset, limit);
		int totalCount = jeonseLoanMapper.countJeonseLoansWithFilter(request);
		List<JeonseLoanResponseDTO> content = jeonseLoans.stream()
			.map(loan -> {
				FinancialCompany companyEntity = financialCompanyService.getById(loan.getFinancialCompanyId());
				String companyName = companyEntity.getName();
				String companyCode = companyEntity.getCode();
				BigDecimal selectedRate = getSelectedRate(loan, request.sortBy());
				return JeonseLoanResponseDTO.from(loan, companyName, companyCode, selectedRate);
			})
			.collect(Collectors.toList());
		return new PageImpl<>(content, pageable, totalCount);
	}

	// 전세대출 상품 상세 조회
	@Override
	public JeonseLoanDetailResponseDTO getJeonseLoanDetail(Long jeonseLoanId, Member member) {
		JeonseLoan jeonseLoan = jeonseLoanMapper.findById(jeonseLoanId)
			.orElseThrow(() -> new IllegalArgumentException("해당 전세대출 상품이 없습니다." + jeonseLoanId));
		FinancialCompany companyEntity = financialCompanyService.getById(jeonseLoan.getFinancialCompanyId());
		FinancialCompanyResponseDTO company = FinancialCompanyResponseDTO.fromEntity(companyEntity);

		Boolean joinAvailable = checkJoinAvailability(jeonseLoan, member);

		return JeonseLoanDetailResponseDTO.from(jeonseLoan, company, joinAvailable);
	}

	private Boolean checkJoinAvailability(JeonseLoan jeonseLoan, Member member) {
		if (member == null) {
			return null;
		}

		if (member.getRemainTime() != null &&
			jeonseLoan.getMinPeriodMonths() != null) {

			LocalDate now = LocalDate.now();
			LocalDate remainTime = member.getRemainTime();

			if (remainTime.isBefore(now)) {
				return false;
			}

			long remaingMonths = ChronoUnit.MONTHS.between(now, remainTime);

			if (remaingMonths < jeonseLoan.getMinPeriodMonths()) {
				return false;
			}
		}
		return true;
	}

	private BigDecimal getSelectedRate(JeonseLoan loan, JeonseLoanRateType rateType) {
		if (rateType == null) {
			return loan.getAvgRate(); // 기본값: 평균금리
		}

		return switch (rateType) {
			case MIN_RATE -> loan.getMinRate();
			case MAX_RATE -> loan.getMaxRate();
			case AVG_RATE -> loan.getAvgRate();
		};
	}

}
