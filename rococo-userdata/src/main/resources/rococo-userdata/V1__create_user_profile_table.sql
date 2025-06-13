USE `rococo-userdata`;

-- Включение необходимых функций для UUID
SET GLOBAL log_bin_trust_function_creators = 1;

-- Таблица профилей пользователей
CREATE TABLE IF NOT EXISTS `user_profile` (
    id          BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    username    VARCHAR(50) UNIQUE NOT NULL,
    firstname   VARCHAR(100),
    lastname    VARCHAR(100),
    avatar      LONGBLOB COMMENT 'Бинарные данные изображения',

    INDEX idx_username (username),
    INDEX idx_name (firstname, lastname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Профили пользователей с дополнительными данными';