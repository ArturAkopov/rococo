SET NAMES utf8mb4;

-- Создание баз данных
CREATE DATABASE IF NOT EXISTS `rococo-auth`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS `rococo-userdata`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS `rococo-art`
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Включение необходимых функций для UUID
SET GLOBAL log_bin_trust_function_creators = 1;

-- =============================================
-- БД rococo-auth (аутентификация и авторизация)
-- =============================================
USE `rococo-auth`;

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS `user` (
    id                      BINARY(16) UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
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
    id        BINARY(16) UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    user_id   BINARY(16) NOT NULL,
    authority ENUM('READ', 'WRITE', 'ADMIN') NOT NULL COMMENT 'Роли доступа',

    UNIQUE INDEX uq_authority_user (user_id, authority),
    CONSTRAINT fk_authority_user
      FOREIGN KEY (user_id) REFERENCES `user` (id)
      ON DELETE CASCADE
      ON UPDATE RESTRICT,
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Таблица прав доступа пользователей';

-- =============================================
-- БД rococo-userdata (профили пользователей)
-- =============================================
USE `rococo-userdata`;

-- Таблица профилей пользователей
CREATE TABLE IF NOT EXISTS `user_profile` (
    id          BINARY(16) UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    username    VARCHAR(50) UNIQUE NOT NULL,
    firstname   VARCHAR(255),
    lastname    VARCHAR(255),
    avatar      LONGBLOB COMMENT 'Бинарные данные изображения',

    INDEX idx_username (username),
    INDEX idx_name (firstname, lastname)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Профили пользователей с дополнительными данными';

-- =============================================
-- БД rococo-art (информация о объектах искусства)
-- =============================================
USE `rococo-art`;

-- Таблица информации о художниках
CREATE TABLE IF NOT EXISTS `artist` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    name        VARCHAR(255)  UNIQUE NOT NULL,
    biography   VARCHAR(2000) NOT NULL,
    photo      LONGBLOB COMMENT 'Бинарные данные изображения',

    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Профили художников с дополнительными данными';

-- Создание таблицы стран
CREATE TABLE IF NOT EXISTS `country` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    name        VARCHAR(255) UNIQUE NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Список стран';

-- Создание таблицы с музеями
CREATE TABLE IF NOT EXISTS `museum` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    title       VARCHAR(255)  UNIQUE NOT NULL,
    description VARCHAR(2000),
    city        VARCHAR(255),
    photo       LONGBLOB COMMENT 'Бинарные данные изображения',
    country_id  BINARY(16)  NOT NULL,
    CONSTRAINT fk_country_id FOREIGN KEY (country_id) REFERENCES `country` (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
      COMMENT='Музеи с дополнительными данными';

-- Создание таблицы с картинами
CREATE TABLE IF NOT EXISTS `painting` (
    id          BINARY(16)  UNIQUE NOT NULL PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), TRUE)),
    title       VARCHAR(255)    NOT NULL,
    description VARCHAR(2000),
    artist_id   BINARY(16)     NOT NULL,
    museum_id   BINARY(16),
    content     LONGBLOB COMMENT 'Бинарные данные изображения',
    CONSTRAINT fk_artist_id FOREIGN KEY (artist_id) REFERENCES `artist` (id),
    CONSTRAINT fk_museum_id FOREIGN KEY (museum_id) REFERENCES `museum` (id)
    );

-- Вставляем страны только если их нет
INSERT IGNORE INTO `country` (name) VALUES
("Австралия"),
('Австрия'),
('Азербайджан'),
('Албания'),
('Алжир'),
('Ангола'),
('Андорра'),
('Антигуа и Барбуда'),
('Аргентина'),
('Армения'),
('Афганистан'),
('Багамские Острова'),
('Бангладеш'),
('Барбадос'),
('Бахрейн'),
('Белиз'),
('Белоруссия'),
('Бельгия'),
('Бенин'),
('Болгария'),
('Боливия'),
('Босния и Герцеговина'),
('Ботсвана'),
('Бразилия'),
('Бруней'),
('Буркина-Фасо'),
('Бурунди'),
('Бутан'),
('Вануату'),
('Великобритания'),
('Венгрия'),
('Венесуэла'),
('Восточный Тимор'),
('Вьетнам'),
('Габон'),
('Республика Гаити'),
('Гайана'),
('Гамбия'),
('Гана'),
('Гватемала'),
('Гвинея'),
('Гвинея-Бисау'),
('Германия'),
('Гондурас'),
('Гренада'),
('Греция'),
('Грузия'),
('Дания'),
('Джибути'),
('Доминика'),
('Доминиканская Республика'),
('Египет'),
('Замбия'),
('Зимбабве'),
('Израиль'),
('Индия'),
('Индонезия'),
('Иордания'),
('Ирак'),
('Иран'),
('Ирландия'),
('Исландия'),
('Испания'),
('Италия'),
('Йемен'),
('Кабо-Верде'),
('Казахстан'),
('Камбоджа'),
('Камерун'),
('Канада'),
('Катар'),
('Кения'),
('Республика Кипр'),
('Киргизия'),
('Кирибати'),
('Китай'),
('Колумбия'),
('Коморы'),
('Республика Конго'),
('Демократическая Республика Конго'),
('Корейская Народно-Демократическая Республика'),
('Республика Корея'),
('Коста-Рика'),
('Кот-д’Ивуар'),
('Куба'),
('Кувейт'),
('Лаос'),
('Латвия'),
('Лесото'),
('Либерия'),
('Ливан'),
('Ливия'),
('Литва'),
('Лихтенштейн'),
('Люксембург'),
('Маврикий'),
('Мавритания'),
('Мадагаскар'),
('Малави'),
('Малайзия'),
('Мали'),
('Мальдивы'),
('Мальта'),
('Марокко'),
('Маршалловы Острова'),
('Мексика'),
('Федеративные Штаты Микронезии'),
('Мозамбик'),
('Молдавия'),
('Монако'),
('Монголия'),
('Мьянма'),
('Намибия'),
('Науру'),
('Непал'),
('Нигер'),
('Нигерия'),
('Нидерланды'),
('Никарагуа'),
('Новая Зеландия'),
('Норвегия'),
('Объединённые Арабские Эмираты'),
('Оман'),
('Пакистан'),
('Палау'),
('Панама'),
('Папуа — Новая Гвинея'),
('Парагвай'),
('Перу'),
('Польша'),
('Португалия'),
('Россия'),
('Руанда'),
('Румыния'),
('Сальвадор'),
('Самоа'),
('Сан-Марино'),
('Сан-Томе и Принсипи'),
('Саудовская Аравия'),
('Флаг Северной Македонии'),
('Сейшельские Острова'),
('Сенегал'),
('Сент-Винсент и Гренадины'),
('Сент-Китс и Невис'),
('Сент-Люсия'),
('Сербия'),
('Сингапур'),
('Сирия'),
('Словакия'),
('Словения'),
('Соединённые Штаты Америки'),
('Соломоновы Острова'),
('Сомали'),
('Судан'),
('Суринам'),
('Сьерра-Леоне'),
('Таджикистан'),
('Таиланд'),
('Танзания'),
('Того'),
('Тонга'),
('Тринидад и Тобаго'),
('Тувалу'),
('Тунис'),
('Туркменистан'),
('Турция'),
('Уганда'),
('Узбекистан'),
('Украина'),
('Уругвай'),
('Фиджи'),
('Филиппины'),
('Финляндия'),
('Франция'),
('Хорватия'),
('Центральноафриканская Республика'),
('Чад'),
('Черногория'),
('Чехия'),
('Чили'),
('Швейцария'),
('Швеция'),
('Флаг Шри-Ланки'),
('Эквадор'),
('Экваториальная Гвинея'),
('Эритрея'),
('Эсватини'),
('Эстония'),
('Эфиопия'),
('Южно-Африканская Республика'),
('Южный Судан'),
('Ямайка'),
('Япония'),
('Ватикан'),
('Палестина');