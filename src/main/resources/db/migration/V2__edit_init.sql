-- V2: 초기 location 테이블 수정
-- 작성일: 2025-07-17
-- 작성자: 박준아

-- 기존 테이블 수정
ALTER TABLE location
    CHANGE COLUMN place_name location_name VARCHAR(255) NOT NULL,
    CHANGE COLUMN place_type location_type ENUM('CENTER', 'CONSULT', 'BANK') NOT NULL;
