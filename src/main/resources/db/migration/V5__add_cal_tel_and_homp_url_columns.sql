-- 금융회사 번호,홈페이지 주소 컬럼 추가
ALTER table financial_company
    add column callnumber varchar(50);


ALTER TABLE financial_company
    ADD COLUMN homp_url VARCHAR(255);
