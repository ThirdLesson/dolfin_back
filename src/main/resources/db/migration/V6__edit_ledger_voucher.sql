ALTER TABLE ledger_voucher
    MODIFY COLUMN transaction_id VARCHAR (36) NOT NULL;

ALTER TABLE ledger_voucher
    MODIFY COLUMN voucher_type ENUM('CHARGE', 'TRANSFER', 'CHARGE_TRANSFER') NOT NULL;

ALTER TABLE ledger_code
    ADD COLUMN name VARCHAR(100) UNIQUE NOT NULL AFTER type;

INSERT INTO ledger_code (account_code_id, name, type)
VALUES (101, 'WALLET_ASSET', 'ASSET'),
       (201, 'BANK_PAYABLE', 'LIABILITY');
