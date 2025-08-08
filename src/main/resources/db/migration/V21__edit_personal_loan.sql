-- 2024-01-15
-- 작성자 : 박준아
-- 설명 : 개인신용대출 테이블 수정 및 데이터 추가
-- 개인신용대출 테이블 수정
-- location 테이블 수정(tel 의 unique 제거)

ALTER TABLE location DROP INDEX uk_location_number;


ALTER TABLE personal_credit_loan
    CHANGE COLUMN personal_credit_loan_id personal_loan_id BIGINT AUTO_INCREMENT,
    CHANGE COLUMN name product_name VARCHAR(255) NOT NULL,
    CHANGE COLUMN crdt_grad_1 base_rate DECIMAL(5, 2) COMMENT '최저금리',
    CHANGE COLUMN crdt_grad_13 max_rate DECIMAL(5, 2) COMMENT '최고금리',
    CHANGE COLUMN crdt_grad_avg spread_rate DECIMAL(5, 2) COMMENT '가산금리 (평균기준용)',
    ADD COLUMN max_loan_amount DECIMAL COMMENT '최대한도' AFTER spread_rate,
    ADD COLUMN min_period_months INT COMMENT '최소기간(개월)' AFTER max_loan_amount,
    ADD COLUMN max_period_months INT COMMENT '최대기간(개월)' AFTER min_period_months,
    ADD COLUMN rate_info VARCHAR(255) COMMENT '금리정보 표시용' AFTER max_period_months,
    ADD COLUMN loan_conditions TEXT COMMENT '대출조건 설명' AFTER rate_info,
    ADD COLUMN foreigner_available TINYINT(1) DEFAULT 1 AFTER loan_conditions,
    ADD COLUMN visa_min_months INT COMMENT '최소 비자기간(개월)' AFTER foreigner_available,
    ADD COLUMN is_active TINYINT(1) DEFAULT 1 AFTER visa_min_months,
DROP COLUMN join_way,
    DROP COLUMN crdt_prdt_type_nm,
    DROP COLUMN cb_name,
    DROP COLUMN crdt_grad_4,
    DROP COLUMN crdt_grad_5,
    DROP COLUMN crdt_grad_6,
    DROP COLUMN crdt_grad_10,
    DROP COLUMN crdt_grad_11,
    DROP COLUMN crdt_grad_12;




INSERT INTO personal_credit_loan (
    financial_company_id,
    product_name,
    base_rate,
    max_rate,
    spread_rate,
    max_loan_amount,
    min_period_months,
    max_period_months,
    rate_info,
    loan_conditions,
    foreigner_available,
    visa_min_months,
    is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0810000'), -- 하나은행
             'CSS대출',
             5.252, -- 최저 (COFIX 신잔액)
             6.592, -- 최고 (6개월물 금융채)
             5.922, -- 평균
             100000000,
             12,
             36,
             '연 5.252% ~ 6.592%',
             'CSS 신용평가 기반 무보증 대출, 신용등급별 차등 금리',
             true,
             null,
             true
         );

-- 전북은행 JB Bravo KOREA 대출
INSERT INTO personal_credit_loan (
    financial_company_id,
    product_name,
    base_rate,
    max_rate,
    spread_rate,
    max_loan_amount,
    min_period_months,
    max_period_months,
    rate_info,
    loan_conditions,
    foreigner_available,
    visa_min_months,
    is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0370000'), -- 전북은행
             'JB Bravo KOREA 대출',
             7.27, -- 기준금리 기준 가산금리 평균
             9.70, -- 최고금리 (신용등급 7등급 기준)
             8.485, -- 평균 가산금리 추정 (7.27 + 9.70) / 2
             20000000, -- 최대 2천만원 한도
             12,
             36,
             '연 7.27% ~ 9.70%',
             '외국인근로자(F-1~F-6 비자 등) 대상 무보증 신용대출, 외국인등록증 1년 이상 및 유효기간 3개월 이상 필요, 재직/소득조건 등 추가 심사',
             true,
             3,
             true
         );

-- 광주은행 TOGETHER외국인신용대출
INSERT INTO personal_credit_loan (
    financial_company_id,
    product_name,
    base_rate,
    max_rate,
    spread_rate,
    max_loan_amount,
    min_period_months,
    max_period_months,
    rate_info,
    loan_conditions,
    foreigner_available,
    visa_min_months,
    is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0340000'), -- 광주은행
             'TOGETHER외국인신용대출',
             12.0, -- 우대금리 적용 시 최저금리
             15.0, -- 기본금리 최대값
             13.5, -- 평균 추정 (12.0 + 15.0) / 2
             50000000, -- 최대 5천만원
             3,
             36,
             '연 12.0% ~ 15.0%',
             '국내 장기체류 등록 외국인 및 거소 신고 동포 대상. 비자(E1~E3, E7, E9, E10, F2, F4, F5, F6)별 재직기간·연소득(2천만원 이상)·잔여 체류기간(6개월 이상) 조건 필요. 중도상환수수료 0.33%, 대출만기일은 체류만기 3개월 전까지. 우대금리 최대 1.0% 적용 가능',
             true,
             6,
             true
         );

-- KB국민은행 SOHO CSS 사이버론
INSERT INTO personal_credit_loan (
    financial_company_id,
    product_name,
    base_rate,
    max_rate,
    spread_rate,
    max_loan_amount,
    min_period_months,
    max_period_months,
    rate_info,
    loan_conditions,
    foreigner_available,
    visa_min_months,
    is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0040000'), -- KB국민은행
             'SOHO CSS 사이버론 (개인사업자 인터넷 기업대출)',
             7.0, -- 예상 최저금리
             10.0, -- 예상 최고금리
             8.5, -- 평균 추정값
             200000000, -- 최고 2억원
             12, -- 1년
             60, -- 5년
             '연 7.0% ~ 10.0% (심사 후 결정)',
             '개인사업자(1년이상) 대상, 금융위원회/금융감독원 고객대출 상담 후 이용 가능. 담보/보증인 불필요. 외국인 가능(외국인등록증, 여권 필요). CSS 신용평가 기반.',
             true,
             null, -- 비자 기간 요구사항 명시 없음
             true
         );


-- KB국민은행 i-ONE 직장인스마트론
INSERT INTO personal_credit_loan (
    financial_company_id,
    product_name,
    base_rate,
    max_rate,
    spread_rate,
    max_loan_amount,
    min_period_months,
    max_period_months,
    rate_info,
    loan_conditions,
    foreigner_available,
    visa_min_months,
    is_active
) VALUES (
             (SELECT financial_company_id FROM financial_company WHERE code = '0200000'), -- 우리 은행
             'i-ONE 직장인스마트론',
             2.520, -- 최저금리 (금융채1년 기준)
             7.245, -- 최고금리
             4.883, -- 평균 (2.520 + 7.245) / 2
             150000000, -- 최대 1억5천만원
             12, -- 1년
             120, -- 10년
             '연 2.520% ~ 7.245%',
             '외국인근로자(E-9, E-10, H-2) 및 재외동포(F-4) 비자 소지자 가능. 1억5천만원 한도. 급여소득자 대상. CSS 신용평가 기반. 우대금리 최대 1.1%',
             true,
             null, -- 비자 기간 요구사항 명시 없음
             true
         );
