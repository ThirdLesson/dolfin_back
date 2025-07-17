package org.scoula.domain.financialproduct.financialcompany.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scoula.domain.financialproduct.financialcompany.dto.request.FinancialCompanyRequestDTO;
import org.scoula.domain.financialproduct.financialcompany.dto.response.FinancialCompanyResponseDTO;
import org.scoula.domain.financialproduct.financialcompany.entity.FinancialCompany;
import org.scoula.domain.financialproduct.financialcompany.mapper.FinancialCompanyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class FinancialCompanyServiceImpl implements FinancialCompanyService {

    private final FinancialCompanyMapper financialCompanyMapper;

    @Transactional
    @Override
    public void create(FinancialCompanyRequestDTO requestDTO) {
        FinancialCompany entity = requestDTO.toEntity();
        financialCompanyMapper.insert(entity);
    }

    @Override
    public FinancialCompanyResponseDTO getById(Long id) {
        FinancialCompany entity = financialCompanyMapper.findById(id);
        return FinancialCompanyResponseDTO.fromEntity(entity);
    }

    @Override
    public List<FinancialCompanyResponseDTO> getAll() {
        List<FinancialCompany> list = financialCompanyMapper.findAll();
        return list.stream()
                .map(FinancialCompanyResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void update(Long id, FinancialCompanyRequestDTO requestDTO) {
        FinancialCompany entity = requestDTO.toEntity();
        entity = FinancialCompany.builder()
                .financialCompanyId(id)
                .name(entity.getName())
                .code(entity.getCode())
                .build();
        financialCompanyMapper.update(entity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        financialCompanyMapper.delete(id);
    }
}

