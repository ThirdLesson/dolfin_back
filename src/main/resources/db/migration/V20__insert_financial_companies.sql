INSERT INTO dolfin.financial_company (code, name, created_at, updated_at, callnumber, homp_url)
VALUES
  ('0040000', '국민은행', NOW(), NOW(), '1588-9999', 'http://www.kbstar.com'),
  ('0260000', '신한은행', NOW(), NOW(), '1577-8000', 'http://www.shinhan.com'),
  ('0810000', '하나은행', NOW(), NOW(), '1599-1111', 'http://www.hanabank.com'),
  ('0200000', '우리은행', NOW(), NOW(), '1588-5000', 'https://spot.wooribank.com/pot/Dream?withyou=po'),
  ('0100000', '농협은행', NOW(), NOW(), '1661-3000', 'https://banking.nonghyup.com'),
  ('0030000', '기업은행', NOW(), NOW(), '1566-2566', 'http://www.ibk.co.kr'),
  ('0020000', '산업은행', NOW(), NOW(), '1588-1500', 'https://www.kdb.co.kr'),
  ('8000000', '신협은행', NOW(), NOW(), '1566-6000', 'https://www.cu.co.kr'),
  ('0230000', '제일은행', NOW(), NOW(), '1588-1599', 'http://www.standardchartered.co.kr'),
  ('0710000', '우체국', NOW(), NOW(), '1599-1900', 'https://www.epostbank.go.kr'),
  ('0320000', '부산은행', NOW(), NOW(), '1588-6200', 'http://www.busanbank.co.kr'),
  ('0310000', '아이엠뱅크', NOW(), NOW(), '1588-5050', 'http://www.imbank.co.kr'),
  ('0070000', '수협은행', NOW(), NOW(), '1588-1515', 'http://www.suhyup-bank.com'),
  ('0390000', '경남은행', NOW(), NOW(), '1600-8585', 'https://www.knbank.co.kr/ib20/mnu/FPMDPT020000000'),
  ('0900000', '카카오뱅크', NOW(), NOW(), '1599-3333', 'https://www.kakaobank.com/'),
  ('0340000', '광주은행', NOW(), NOW(), '1588-3388', 'http://www.kjbank.com'),
  ('0920000', '토스뱅크', NOW(), NOW(), '1661-7654', 'https://www.tossbank.com/product-service/savings/account'),
  ('0370000', '전북은행', NOW(), NOW(), '1588-4477', 'https://www.jbbank.co.kr/EFINANCE_MAIN.act'),
  ('0890000', '케이뱅크', NOW(), NOW(), '1522-1000', 'https://www.kbanknow.com'),
  ('0350000', '제주은행', NOW(), NOW(), '1588-0079', 'https://www.jejubank.co.kr/hmpg/main.do')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  updated_at = VALUES(updated_at),
  callnumber = VALUES(callnumber),
  homp_url = VALUES(homp_url);
