ALTER TABLE transaction
    ADD COLUMN counter_party_name VARCHAR(20),
    ADD COLUMN counter_party_bank_type VARCHAR(10),
    ADD COLUMN counter_party_account_number VARCHAR(30);

ALTER TABLE transaction
    MODIFY COLUMN transaction_type ENUM('CHARGE', 'ACCOUNT_TRANSFER', 'WALLET_TRANSFER');

ALTER TABLE ledger_voucher
    ADD COLUMN type VARCHAR(50) NOT NULL