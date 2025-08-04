ALTER TABLE account
    MODIFY COLUMN bank_type ENUM(
    '산업은행','기업은행','국민은행','수협은행','농협은행',
    '우리은행','SC은행','제일은행','씨티은행','대구은행','부산은행',
    '광주은행','제주은행','전북은행','경남은행','새마을금고',
    '신협은행','우체국','KEB하나은행','하나은행','신한은행',
    'K뱅크','케이뱅크','카카오뱅크','토스뱅크'
    )
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;
