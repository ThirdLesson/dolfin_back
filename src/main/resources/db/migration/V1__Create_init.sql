-- V1: 초기 member 테이블 생성
-- 작성일: 2025-07-17
-- 작성자: 박준아


-- Member table
CREATE TABLE member
(
    member_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    login_id        VARCHAR(100) NOT NULL,
    password        VARCHAR(100) NOT NULL,
    passport_number VARCHAR(100) NOT NULL,
    nationality     VARCHAR(100) NOT NULL,
    birth           DATE         NOT NULL,
    name            VARCHAR(100) NOT NULL,
    phone_number    VARCHAR(50),
    remain_time     DATE,
    currency        VARCHAR(20),
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Wallet table
CREATE TABLE wallet
(
    wallet_id  BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id  BIGINT         NOT NULL,
    balance    DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    password   INT,
    created_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- Account table
CREATE TABLE account
(
    account_id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id      BIGINT         NOT NULL,
    member_id      BIGINT         NOT NULL,
    account_number VARCHAR(30)    NOT NULL,
    password       VARCHAR(30)    NOT NULL,
    balance        DECIMAL(18, 2) NOT NULL DEFAULT 0.00,
    bank_code      VARCHAR(15),
    bank_name      VARCHAR(20),
    is_verified    BOOLEAN                 DEFAULT FALSE,
    verified_at    TIMESTAMP,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallet (wallet_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- Remittance_group table
CREATE TABLE remittance_group
(
    remittance_group_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                 VARCHAR(100),
    currency              VARCHAR(20),
    date                  TIMESTAMP,
    count                 INT,
    total_amount          DECIMAL(18, 2),
    applied_exchange_rate DECIMAL(18, 6),
    created_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Member_remittance_group table
CREATE TABLE member_remittance_group
(
    member_remittance_group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    remittance_group_id        BIGINT   NOT NULL,
    member_id                  BIGINT   NOT NULL,
    receiver_bank              VARCHAR(100),
    swift_code                 VARCHAR(100),
    router_code                VARCHAR(100),
    receiver_account           VARCHAR(100),
    receiver_name              VARCHAR(100),
    receiver_nationality       VARCHAR(100),
    receiver_address           VARCHAR(255),
    purpose                    VARCHAR(255),
    amount                     BIGINT,
    exchange_amount            BIGINT,
    status                     ENUM('PENDING', 'SUCCESS', 'FAILED'),
    created_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                 DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (remittance_group_id) REFERENCES remittance_group (remittance_group_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- Transaction table
CREATE TABLE transaction
(
    transaction_id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    wallet_id              BIGINT         NOT NULL,
    member_id              BIGINT         NOT NULL,
    transaction_group_id   CHAR(36),
    amount                 DECIMAL(18, 2) NOT NULL,
    before_balance         DECIMAL(18, 2) NOT NULL,
    after_balance          DECIMAL(18, 2) NOT NULL,
    transaction_type       ENUM('CHARGE', 'TRANSFER'),
    counterparty_member_id BIGINT,
    counterparty_wallet_id BIGINT,
    status                 ENUM('PENDING', 'SUCCESS', 'FAILED'),
    created_at             DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (wallet_id) REFERENCES wallet (wallet_id),
    FOREIGN KEY (member_id) REFERENCES member (member_id)
);

-- 1. 전표 테이블
CREATE TABLE ledger_voucher
(
    ledger_voucher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no        VARCHAR(30) NOT NULL,
    transaction_id    BIGINT      NOT NULL,
    entry_date        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    voucher_type      ENUM('CHARGE', 'TRANSFER') NOT NULL,
    created_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- 2. 회계 코드 테이블
CREATE TABLE ledger_code
(
    account_code_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type            ENUM('ASSET', 'LIABILITY', 'EQUITY', 'INCOME', 'EXPENSE') NOT NULL,
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. 분개 테이블
CREATE TABLE ledger_entry
(
    ledger_entry_id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    ledger_voucher_id BIGINT         NOT NULL,
    ledger_type       ENUM('DEBIT', 'CREDIT') NOT NULL,
    account_code_id   BIGINT         NOT NULL,
    amount            DECIMAL(18, 2) NOT NULL,
    created_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_entry_voucher
        FOREIGN KEY (ledger_voucher_id) REFERENCES ledger_voucher (ledger_voucher_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_entry_account_code
        FOREIGN KEY (account_code_id) REFERENCES ledger_code (account_code_id)
            ON DELETE RESTRICT
);

-- 금융회사 테이블
CREATE TABLE financial_company
(
    financial_company_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code                 VARCHAR(255) NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    created_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 예금 적금 테이블
CREATE TABLE deposit_saving
(
    deposit_saving_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    financial_company_id BIGINT         NOT NULL,
    type                 ENUM('DEPOSIT', 'SAVING') NOT NULL, -- 예/적금
    name                 VARCHAR(255)   NOT NULL,
    join_way             VARCHAR(255),
    interest_description VARCHAR(255),
    spcl_condition       VARCHAR(255),
    ctc_note             VARCHAR(1024),
    max_limit            VARCHAR(255),
    save_month           INT            NOT NULL,
    interest_rate        DECIMAL(10, 2) NOT NULL,
    max_interest_rate    DECIMAL(10, 2) NOT NULL,
    interest_rate_type   VARCHAR(255)   NOT NULL,
    reserve_type         VARCHAR(255),
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (financial_company_id) REFERENCES financial_company (financial_company_id)
);

-- 전세자금대출 테이블
CREATE TABLE jeonse_loan
(
    jeonse_loan_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    financial_company_id BIGINT         NOT NULL,
    name                 VARCHAR(255)   NOT NULL,
    join_way             VARCHAR(255),
    loan_expensive       VARCHAR(255),
    erly_fee             VARCHAR(255),
    dly_rate             VARCHAR(255)   NOT NULL,
    loan_lmt             VARCHAR(255),
    repay_type_name      VARCHAR(255),
    lend_rate_type_name  VARCHAR(255),
    lend_rate_min        DECIMAL(10, 2) NOT NULL,
    lend_rate_max        DECIMAL(10, 2) NOT NULL,
    lend_rate_avg        DECIMAL(10, 2) NOT NULL,
    created_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (financial_company_id) REFERENCES financial_company (financial_company_id)
);

CREATE TABLE personal_credit_loan
(
    personal_credit_loan_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    financial_company_id    BIGINT       NOT NULL,
    name                    VARCHAR(255) NOT NULL,
    join_way                VARCHAR(255),
    crdt_prdt_type_nm       VARCHAR(255),
    cb_name                 VARCHAR(255) NOT NULL,
    crdt_grad_1             DECIMAL(10, 2),
    crdt_grad_4             DECIMAL(10, 2),
    crdt_grad_5             DECIMAL(10, 2),
    crdt_grad_6             DECIMAL(10, 2),
    crdt_grad_10            DECIMAL(10, 2),
    crdt_grad_11            DECIMAL(10, 2),
    crdt_grad_12            DECIMAL(10, 2),
    crdt_grad_13            DECIMAL(10, 2),
    crdt_grad_avg           DECIMAL(10, 2),
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (financial_company_id) REFERENCES financial_company (financial_company_id)
);

-- 위치 (Location) 테이블
CREATE TABLE location
(
    location_id     BIGINT       NOT NULL AUTO_INCREMENT,
    address         VARCHAR(255) NOT NULL,
    point           POINT        NOT NULL,
    location_number VARCHAR(30)  NOT NULL,
    place_type      ENUM('대문화시설센터', '상업소', '학교', '은행', '병원') NOT NULL,
    place_name      VARCHAR(255) NOT NULL,
    PRIMARY KEY (location_id),
    UNIQUE KEY uk_location_number (location_number),
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

-- 환율 (Exchange_rate) 테이블
CREATE TABLE exchange_rate
(
    exchange_id     BIGINT         NOT NULL AUTO_INCREMENT,
    base_exchange   CHAR(3)        NOT NULL,
    target_exchange CHAR(3)        NOT NULL,
    exchange_value  DECIMAL(10, 4) NOT NULL,
    bank_name       VARCHAR(100)   NOT NULL,
    exchange_date   DATE           NOT NULL,
    type            ENUM('GETCASH', 'SELLCASH', 'SEND', 'RECEIVE','BASE') NOT NULL,
    PRIMARY KEY (exchange_id),
    created_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);