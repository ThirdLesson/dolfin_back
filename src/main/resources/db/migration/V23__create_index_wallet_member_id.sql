-- V23: add unique index on wallet(member_id) if absent
SET @idx := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name   = 'wallet'
    AND index_name   = 'ux_wallet_member_id'
);

SET @sql := IF(@idx = 0,
  'ALTER TABLE wallet ADD UNIQUE KEY ux_wallet_member_id (member_id)',
  'DO 0'  -- no-op
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;