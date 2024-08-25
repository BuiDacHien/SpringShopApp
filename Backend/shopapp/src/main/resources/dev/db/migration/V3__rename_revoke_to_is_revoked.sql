ALTER TABLE tokens
    CHANGE COLUMN `revoke` `is_revoked` TINYINT(1) NOT NULL;