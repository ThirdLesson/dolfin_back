-- 예/적금 테이블 삭제
DROP TABLE IF EXISTS deposit_saving;

-- 예금 테이블 생성

CREATE TABLE deposit (
                         deposit_id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                         financial_company_id  BIGINT NOT NULL,
                         name                  VARCHAR(255) NOT NULL,
                         save_month            INT NOT NULL,
                         interest_rate         DECIMAL(10, 2) NOT NULL,
                         max_interest_rate     DECIMAL(10, 2) NOT NULL,
                         created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                         FOREIGN KEY (financial_company_id) REFERENCES financial_company(financial_company_id)
);
-- 우대조건 테이블
CREATE TABLE deposit_spcl_condition (
                                        id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        deposit_id    BIGINT NOT NULL,
                                        spcl_condition     VARCHAR(100) NOT NULL,
                                        FOREIGN KEY (deposit_id) REFERENCES deposit(deposit_id) ON DELETE CASCADE
);

-- 중복저장방지
ALTER TABLE deposit
    ADD CONSTRAINT uq_company_name_month
        UNIQUE (financial_company_id, name, save_month);


ALTER TABLE deposit
    ADD COLUMN product_code VARCHAR(100) NOT NULL,
    ADD COLUMN company_code VARCHAR(100) NOT NULL;

ALTER TABLE deposit_spcl_condition
    ADD COLUMN product_code VARCHAR(100) NOT NULL,
    ADD COLUMN company_code VARCHAR(100) NOT NULL;

ALTER TABLE deposit_spcl_condition
    ADD COLUMN save_month INT,
    ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

