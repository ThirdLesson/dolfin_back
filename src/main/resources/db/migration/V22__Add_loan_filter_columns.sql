-- 전세대출 테이블 컬럼 추가 (최소 필요 잔여 체류기간(개월),최대 대출기간(개월),최소 대출금액(원),최대 대출금액(원))
ALTER TABLE jeonse_loan
    ADD COLUMN min_remaining_visa_months INT,
    ADD COLUMN max_loan_term_months INT,
    ADD COLUMN loan_min_amount BIGINT,
    ADD COLUMN loan_max_amount BIGINT;
