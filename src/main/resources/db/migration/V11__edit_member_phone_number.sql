ALTER TABLE member
ADD CONSTRAINT uq_member_phone_number UNIQUE (phone_number);
