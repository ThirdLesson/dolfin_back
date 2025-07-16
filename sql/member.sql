drop table if exists member;
CREATE TABLE member (
                        member_id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 회원 아이디 (PK)
                        login_id VARCHAR(50) NOT NULL UNIQUE,          -- 로그인 아이디 (중복 불가)
                        password VARCHAR(255) NOT NULL,                -- 비밀번호 (암호화 고려)
                        passport_number VARCHAR(20),                   -- 여권번호
                        nationality VARCHAR(50),                       -- 국적
                        birth DATE,                                     -- 생년월일
                        name VARCHAR(100),                             -- 성명
                        phone_number VARCHAR(20),                      -- 전화번호
                        remain_time DATE,                               -- 잔여 체류기간 (잔여일이 아닌 잔여일자)
                        currency VARCHAR(10),                          -- 설정 통화 (예: KRW, USD 등)
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 생성일자 (옵션)
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정일자 (옵션)
);
