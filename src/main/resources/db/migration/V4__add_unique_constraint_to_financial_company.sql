-- 금융회사 코드에 유니크 제약 조건 추가
ALTER TABLE financial_company
    ADD CONSTRAINT uq_code UNIQUE (code);

-- 예/적금 크기 확장
ALTER TABLE deposit_saving
    MODIFY COLUMN spcl_condition TEXT,
    MODIFY COLUMN interest_description TEXT,
    MODIFY COLUMN ctc_note TEXT,
    MODIFY COLUMN name VARCHAR(500),
    MODIFY COLUMN join_way VARCHAR(500);


-- max_interest_rate 컬럼을 NULL 허용으로 변경
ALTER TABLE deposit_saving MODIFY COLUMN max_interest_rate DECIMAL(5,2) NULL;

-- interest_rate 컬럼도 함께 확인
ALTER TABLE deposit_saving MODIFY COLUMN interest_rate DECIMAL(5,2) NULL;

