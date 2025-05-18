-- Включение необходимых функций для UUID
SET GLOBAL log_bin_trust_function_creators = 1;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS `user` (
    id                      BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    username                VARCHAR(50) UNIQUE NOT NULL,
    password                VARCHAR(255) NOT NULL COMMENT 'Хранить только хэши (bcrypt/scrypt)',
    enabled                 BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired     BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked      BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,

    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Таблица пользователей для Spring Security';

-- Таблица прав доступа
CREATE TABLE IF NOT EXISTS `authority` (
    id        BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    user_id   BINARY(16) NOT NULL,
    authority ENUM('READ', 'WRITE') NOT NULL COMMENT 'Базовые роли доступа',

    UNIQUE INDEX uq_authority_user (user_id, authority),
    CONSTRAINT fk_authority_user
      FOREIGN KEY (user_id) REFERENCES `user` (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Таблица прав доступа пользователей';