ALTER TABLE member
    ADD COLUMN connected_id VARCHAR(70);


-- bank_code, bank_name 컬럼 삭제
ALTER TABLE account
DROP COLUMN bank_code,
DROP COLUMN bank_name;

-- ENUM 타입 생성
ALTER TABLE account
    ADD COLUMN bank_type ENUM(
    '산업은행', '기업은행', '국민은행', '수협은행', '농협은행',
    '우리은행', 'SC은행', '씨티은행', '대구은행', '부산은행',
    '광주은행', '제주은행', '전북은행', '경남은행', '새마을금고',
    '신협은행', '우체국', 'KEB하나은행', '신한은행', 'K뱅크'
) AFTER password;

ALTER TABLE account
    ADD CONSTRAINT uq_account_account_number UNIQUE (account_number);