USE `rococo-userdata`;

-- Включение необходимых функций для UUID
SET GLOBAL log_bin_trust_function_creators = 1;

-- Таблица информации о художниках
CREATE TABLE IF NOT EXISTS `artist` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    name        VARCHAR(255)  UNIQUE NOT NULL,
    biography   VARCHAR(2000) NOT NULL,
    photo      LONGBLOB COMMENT 'Бинарные данные изображения',

    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Профили художников с дополнительными данными';