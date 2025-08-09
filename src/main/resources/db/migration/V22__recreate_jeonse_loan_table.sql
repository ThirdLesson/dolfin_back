-- V22__recreate_jeonse_loan_table.sql

-- 기존 jeonse_loan 테이블 삭제 (외래키 먼저 끊기)
DROP TABLE IF EXISTS jeonse_loan;

-- 전세자금대출 테이블 재생성
CREATE TABLE jeonse_loan (
                             jeonse_loan_id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '전세자금대출 ID',
                             financial_company_id   BIGINT NOT NULL COMMENT '금융회사 ID',
                             product_name           VARCHAR(255) NOT NULL COMMENT '상품명',
                             join_way               VARCHAR(255) COMMENT '가입방법',
                             repay_type_name        VARCHAR(255) COMMENT '상환유형명',
                             lend_rate_type_name    VARCHAR(255) COMMENT '대출금리유형명',
                             base_rate              DECIMAL(10, 2) NOT NULL COMMENT '최저금리',
                             max_rate               DECIMAL(10, 2) NOT NULL COMMENT '최고금리',
                             avg_rate               DECIMAL(10, 2) NOT NULL COMMENT '평균금리',
                             visa_min_months        INT NULL COMMENT '최소 비자기간(개월)',
                             max_period_months      INT NULL COMMENT '최대기간(개월)',
                             min_period_months      INT NULL COMMENT '최소기간(개월)',
                             min_loan_amount        BIGINT NULL COMMENT '최소대출금액(원)',
                             max_loan_amount        BIGINT NULL COMMENT '최대대출금액(원)',
                             rate_info              VARCHAR(255) NULL COMMENT '금리정보 표시용',
                             loan_conditions        TEXT NULL COMMENT '대출조건 설명',
                             foreigner_available    TINYINT(1) NOT NULL DEFAULT 1 COMMENT '외국인 가능 여부',
                             is_active              TINYINT(1) NOT NULL DEFAULT 1 COMMENT '활성 여부',
                             created_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
                             updated_at             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
                             FOREIGN KEY (financial_company_id) REFERENCES financial_company(financial_company_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='전세자금대출 테이블';
