package org.scoula.domain.financialCompany.jeonseLoan.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialCompany.jeonseLoan.dto.request.JeonseLoanRequestDTO;
import org.scoula.domain.financialCompany.jeonseLoan.dto.response.JeonseLoanResponseDTO;
import org.scoula.domain.financialCompany.jeonseLoan.entity.JeonseLoan;
import org.scoula.domain.financialCompany.jeonseLoan.mapper.JeonseLoanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JeonseLoanServiceImpl implements JeonseLoanService {

    private final JeonseLoanMapper jeonseLoanMapper;

    @Transactional
    @Override
    public void createJeonseLoan(JeonseLoanRequestDTO requestDTO) {
        jeonseLoanMapper.insertJeonseLoan(requestDTO.toEntity());
    }

    @Override
    public JeonseLoanResponseDTO getJeonseLoanById(Long jeonseLoanId) {
        JeonseLoan jeonseLoan = jeonseLoanMapper.selectJeonseLoanById(jeonseLoanId);
        return JeonseLoanResponseDTO.fromEntity(jeonseLoan);
    }

    @Override
    public List<JeonseLoanResponseDTO> getAllJeonseLoans() {
        List<JeonseLoan> list = jeonseLoanMapper.selectAllJeonseLoans();
        return list.stream()
                .map(JeonseLoanResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateJeonseLoan(Long jeonseLoanId, JeonseLoanRequestDTO requestDTO) {
        JeonseLoan jeonseLoan = requestDTO.toEntity();
        jeonseLoanMapper.updateJeonseLoan(jeonseLoanId, jeonseLoan);
    }

    @Transactional
    @Override
    public void deleteJeonseLoan(Long jeonseLoanId) {
        jeonseLoanMapper.deleteJeonseLoan(jeonseLoanId);
    }
}
