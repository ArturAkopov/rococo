USE `rococo-art`;

-- Включение необходимых функций для UUID
SET GLOBAL log_bin_trust_function_creators = 1;

-- Создание таблицы с картинами
CREATE TABLE IF NOT EXISTS `painting` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    title       VARCHAR(255)    NOT NULL,
    description VARCHAR(1000),
    artist_id   BINARY(16)     NOT NULL,
    museum_id   BINARY(16),
    content     LONGBLOB COMMENT 'Бинарные данные изображения',
    CONSTRAINT fk_artist_id FOREIGN KEY (artist_id) REFERENCES `artist` (id),
    CONSTRAINT fk_museum_id FOREIGN KEY (museum_id) REFERENCES `museum` (id)
    );