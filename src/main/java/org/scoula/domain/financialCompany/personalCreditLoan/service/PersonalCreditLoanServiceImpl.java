package org.scoula.domain.financialCompany.personalCreditLoan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialCompany.personalCreditLoan.dto.request.PersonalCreditLoanRequestDTO;
import org.scoula.domain.financialCompany.personalCreditLoan.dto.response.PersonalCreditLoanResponseDTO;
import org.scoula.domain.financialCompany.personalCreditLoan.entity.PersonalCreditLoan;
import org.scoula.domain.financialCompany.personalCreditLoan.mapper.PersonalCreditLoanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonalCreditLoanServiceImpl implements PersonalCreditLoanService {

    private final PersonalCreditLoanMapper personalCreditLoanMapper;

    @Transactional
    @Override
    public void createPersonalCreditLoan(PersonalCreditLoanRequestDTO requestDTO) {
        log.info("createPersonalCreditLoan: {}", requestDTO);
        PersonalCreditLoan entity = requestDTO.toEntity();
        personalCreditLoanMapper.insertPersonalCreditLoan(entity);
    }

    @Override
    public PersonalCreditLoanResponseDTO getPersonalCreditLoanById(Long personalCreditLoanId) {
        log.info("getPersonalCreditLoanById: {}", personalCreditLoanId);
        PersonalCreditLoan entity = personalCreditLoanMapper.selectPersonalCreditLoanById(personalCreditLoanId);
        return PersonalCreditLoanResponseDTO.fromEntity(entity);
    }

    @Override
    public List<PersonalCreditLoanResponseDTO> getAllPersonalCreditLoans() {
        log.info("getAllPersonalCreditLoans");
        List<PersonalCreditLoan> entityList = personalCreditLoanMapper.selectAllPersonalCreditLoans();
        return entityList.stream()
                .map(PersonalCreditLoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updatePersonalCreditLoan(Long personalCreditLoanId, PersonalCreditLoanRequestDTO requestDTO) {
        log.info("updatePersonalCreditLoan: {}, {}", personalCreditLoanId, requestDTO);
        PersonalCreditLoan entity = requestDTO.toEntity();
        // 엔티티에 PK 값 세팅 필요
        entity = PersonalCreditLoan.builder()
                .personalCreditLoanId(personalCreditLoanId)
                .financialCompanyId(entity.getFinancialCompanyId())
                .name(entity.getName())
                .joinWay(entity.getJoinWay())
                .crdtPrdtTypeNm(entity.getCrdtPrdtTypeNm())
                .cbName(entity.getCbName())
                .crdtGrad1(entity.getCrdtGrad1())
                .crdtGrad4(entity.getCrdtGrad4())
                .crdtGrad5(entity.getCrdtGrad5())
                .crdtGrad6(entity.getCrdtGrad6())
                .crdtGrad10(entity.getCrdtGrad10())
                .crdtGrad11(entity.getCrdtGrad11())
                .crdtGrad12(entity.getCrdtGrad12())
                .crdtGrad13(entity.getCrdtGrad13())
                .crdtGradAvg(entity.getCrdtGradAvg())
                .build();
        personalCreditLoanMapper.updatePersonalCreditLoan(entity);
    }

    @Transactional
    @Override
    public void deletePersonalCreditLoan(Long personalCreditLoanId) {
        log.info("deletePersonalCreditLoan: {}", personalCreditLoanId);
        personalCreditLoanMapper.deletePersonalCreditLoan(personalCreditLoanId);
    }
}
