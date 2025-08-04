-- 예/적금 테이블 삭제
DROP TABLE IF EXISTS deposit_saving;

-- 예금 테이블 생성

CREATE TABLE deposit
(
    deposit_id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    financial_company_id BIGINT         NOT NULL,
    name                 VARCHAR(255)   NOT NULL,
    save_month           INT            NOT NULL,
    interest_rate        DECIMAL(10, 2) NOT NULL,
    max_interest_rate    DECIMAL(10, 2) NOT NULL,
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (financial_company_id) REFERENCES financial_company (financial_company_id)
);
-- 우대조건 테이블
CREATE TABLE deposit_spcl_condition
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    deposit_id     BIGINT       NOT NULL,
    spcl_condition VARCHAR(100) NOT NULL,
    FOREIGN KEY (deposit_id) REFERENCES deposit (deposit_id) ON DELETE CASCADE
);
-- 예금 상품 중복 저장 방지를 위한 유니크 제약조건 추가
ALTER TABLE deposit
    ADD CONSTRAINT uq_company_name_month
        UNIQUE (financial_company_id, name, save_month);
-- 예금 상품 테이블에 상품 코드 및 회사 코드 추가 (API 연동 및 식별용)
ALTER TABLE deposit
    ADD COLUMN product_code VARCHAR(100) NOT NULL,
    ADD COLUMN company_code VARCHAR(100) NOT NULL;
-- 예금 우대 조건 테이블에 상품 코드 및 회사 코드 추가 (조인 및 데이터 연계용)
ALTER TABLE deposit_spcl_condition
    ADD COLUMN product_code VARCHAR(100) NOT NULL,
    ADD COLUMN company_code VARCHAR(100) NOT NULL;
-- 예금 우대 조건 테이블에 저축 기간 및 생성/수정일 컬럼 추가 (조건 세분화 및 이력 관리)
ALTER TABLE deposit_spcl_condition
    ADD COLUMN save_month INT,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
-- 금융회사 테이블에 콜센터 번호 및 홈페이지 주소 컬럼 추가 (고객 정보 제공용)
ALTER TABLE financial_company
    ADD COLUMN callnumber VARCHAR(50),
    ADD COLUMN homp_url   VARCHAR(255);
