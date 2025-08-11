-- 1. 신한은행
INSERT INTO jeonse_loan (
    financial_company_id, product_name, join_way,
    base_rate, max_rate, avg_rate,
    visa_min_months, max_period_months, min_period_months,
    min_loan_amount, max_loan_amount,
    rate_info, loan_conditions, foreigner_available, is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0200000'), -- 신한은행
             '신한 더드림 전세대출',
             '영업점 방문',
             2.88, 5.50, 4.20,
             3, 36, 1,
             5000000, 200000000,
             '변동금리 2.88%~5.50%',
             '외국인 등록증, 재직증명서, 소득증빙서류 필요',
             true, true
         );

-- 2. 하나은행 (외국인 근로자)
INSERT INTO jeonse_loan (
    financial_company_id, product_name, join_way,
    base_rate, max_rate, avg_rate,
    visa_min_months, max_period_months, min_period_months,
    min_loan_amount, max_loan_amount,
    rate_info, loan_conditions, foreigner_available, is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0810000'), -- 하나은행
             '하나은행 외국인 근로자 전세자금대출',
             '영업점 방문',
             4.20, 5.80, 5.00,
             3, 37, 1,
             3000000, 60000000,
             '혼합형금리 4.20%~5.80%',
             '외국인 근로자 대상, 서울보증보험 보증서 필수',
             true, true
         );

-- 3. 전북은행
INSERT INTO jeonse_loan (
    financial_company_id, product_name, join_way,
    base_rate, max_rate, avg_rate,
    visa_min_months, max_period_months, min_period_months,
    min_loan_amount, max_loan_amount,
    rate_info, loan_conditions, foreigner_available, is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0370000'), -- 전북은행
             '전북은행 외국인 전세자금대출',
             '영업점 방문',
             4.50, 5.50, 5.00,
             3, 36, 1,
             3000000, 50000000,
             '변동금리 4.50%~5.50%',
             '국내거소신고자, 외국인 등록증 및 재직증명서 필수',
             true, true
         );

-- 4. 국민은행 (KB)
INSERT INTO jeonse_loan (
    financial_company_id, product_name, join_way,
    base_rate, max_rate, avg_rate,
    visa_min_months, max_period_months, min_period_months,
    min_loan_amount, max_loan_amount,
    rate_info, loan_conditions, foreigner_available, is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0040000'), -- 국민은행
             'KB Welcome Plus 전세자금대출',
             '영업점 방문',
             3.17, 5.80, 4.50,
             3, 24, 1,
             5000000, 200000000,
             '변동금리 3.17%~5.80%',
             'SGI서울보증보험 보증심사 통과 필요, 중도상환수수료 없음',
             true, true
         );

-- 5. 하나은행 (우량주택)
INSERT INTO jeonse_loan (
    financial_company_id, product_name, join_way,
    base_rate, max_rate, avg_rate,
    visa_min_months, max_period_months, min_period_months,
    min_loan_amount, max_loan_amount,
    rate_info, loan_conditions, foreigner_available, is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0810000'), -- 하나은행
             '하나은행 우량주택전세론 (외국인)',
             '영업점 방문',
             3.50, 6.00, 4.75,
             3, 37, 1,
             10000000, 200000000,
             '변동금리 3.50%~6.00%',
             '국내거소신고자, 서울보증보험 가입 필수',
             true, true
         );
