-- 1. 자식 테이블 먼저 삭제
DROP TABLE IF EXISTS member_remittance_group;

-- 2. 부모 테이블 삭제
DROP TABLE IF EXISTS remittance_group;

CREATE TABLE remittance_group (
    remittance_group_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    benefit_status      ENUM('ON','OFF')     NULL,
    remittance_date     TINYINT UNSIGNED     NOT NULL CHECK (remittance_date BETWEEN 1 AND 31),
    member_count        INT UNSIGNED    NOT NULL DEFAULT 0,
    currency            VARCHAR(20)     NOT NULL,
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (remittance_group_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE remittance_information (
    remittance_information_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiver_bank VARCHAR(100),
    swift_code VARCHAR(100),
    router_code VARCHAR(100),
    receiver_account VARCHAR(100),
    receiver_name VARCHAR(100),
    receiver_nationality VARCHAR(100),
    receiver_address VARCHAR(255),
    purpose VARCHAR(255),
    amount DECIMAL(18, 2),
    transmit_fail_count INT UNSIGNED NOT NULL DEFAULT 0,
    intermediary_bank_commission ENUM('OUR','SHA','BEN') NOT NULL DEFAULT 'SHA',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE member
    ADD COLUMN remittance_information_id BIGINT NULL,
    ADD COLUMN remittance_group_id       BIGINT UNSIGNED NULL,

    ADD CONSTRAINT fk_member__remittance_information
        FOREIGN KEY (remittance_information_id)
        REFERENCES remittance_information (remittance_information_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
            ADD CONSTRAINT fk_member__remittance_group
            FOREIGN KEY (remittance_group_id)
            REFERENCES remittance_group (remittance_group_id)
           ON DELETE SET NULL
        ON UPDATE CASCADE;

ALTER TABLE remittance_group
    ADD UNIQUE KEY uq_currency_day_status (currency, remittance_date, benefit_status);

ALTER TABLE member
    ADD COLUMN fcm_token VARCHAR(200) NULL;