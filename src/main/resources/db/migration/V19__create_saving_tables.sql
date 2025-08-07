-- 적금 상품 테이블
CREATE TABLE saving (
                        saving_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        financial_company_id BIGINT NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        save_month INT NOT NULL,
                        interest_rate DECIMAL(10, 2) NOT NULL,
                        max_interest_rate DECIMAL(10, 2) NOT NULL,
                        product_code VARCHAR(100) NOT NULL,
                        company_code VARCHAR(100) NOT NULL,
                        created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        FOREIGN KEY (financial_company_id) REFERENCES financial_company(financial_company_id)
);

-- 적금 우대조건 테이블
CREATE TABLE saving_spcl_condition (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       saving_id BIGINT NOT NULL,
                                       spcl_condition VARCHAR(100) NOT NULL,
                                       product_code VARCHAR(100) NOT NULL,
                                       company_code VARCHAR(100) NOT NULL,
                                       save_month INT,
                                       created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                       FOREIGN KEY (saving_id) REFERENCES saving(saving_id) ON DELETE CASCADE
);

-- 적금 상품 중복 저장 방지를 위한 유니크 제약조건
ALTER TABLE saving
    ADD CONSTRAINT uq_saving_company_name_month
        UNIQUE (financial_company_id, name, save_month);
