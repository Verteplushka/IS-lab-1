-- Удаление зависимых таблиц
DROP TABLE IF EXISTS product CASCADE;
DROP TABLE IF EXISTS coordinates CASCADE;
DROP TABLE IF EXISTS organization CASCADE;
DROP TABLE IF EXISTS person CASCADE;
DROP TABLE IF EXISTS address CASCADE;
DROP TABLE IF EXISTS location CASCADE;
DROP TABLE IF EXISTS users_is CASCADE;


CREATE TABLE Coordinates
(
    id SERIAL PRIMARY KEY,
    x  INTEGER NOT NULL,
    y  REAL CHECK (y <= 327)
);

CREATE TABLE Location
(
    id   SERIAL PRIMARY KEY,
    x    INTEGER NOT NULL,
    y    REAL    NOT NULL,
    z    INTEGER NOT NULL,
    name VARCHAR NOT NULL CHECK (name <> '')
);

CREATE TABLE Address
(
    id       SERIAL PRIMARY KEY,
    street   VARCHAR NOT NULL,
    zip_code VARCHAR NOT NULL
);

CREATE TABLE Organization
(
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR              NOT NULL CHECK (name <> ''),
    official_address_id INTEGER              NOT NULL REFERENCES Address (id),
    annual_turnover     REAL                 NOT NULL CHECK (annual_turnover > 0),
    employees_count     INTEGER CHECK (employees_count > 0),
    full_name           VARCHAR(1668) UNIQUE NOT NULL,
    type                VARCHAR(40)          NOT NULL
);

CREATE TABLE Person
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR          NOT NULL CHECK (name <> ''),
    eye_color   VARCHAR(40)      NOT NULL,
    hair_color  VARCHAR(40),
    location_id INTEGER          NOT NULL REFERENCES Location (id),
    weight      DOUBLE PRECISION NOT NULL CHECK (weight > 0),
    nationality VARCHAR(40)      NOT NULL
);

-- Создание таблицы users_is
CREATE TABLE users_is
(
    id       SERIAL PRIMARY KEY,                    -- Автоинкрементируемый идентификатор
    login    VARCHAR(255) NOT NULL UNIQUE,          -- Уникальный логин
    password VARCHAR(255) NOT NULL,                 -- Пароль
    role     VARCHAR(40)  NOT NULL,
    request_admin_rights BOOLEAN DEFAULT FALSE      -- Флаг запроса админских прав
);

CREATE TABLE Product
(
    id               BIGSERIAL PRIMARY KEY,
    name             VARCHAR   NOT NULL CHECK (name <> ''),
    coordinates_id   INTEGER   NOT NULL REFERENCES Coordinates (id),
    creation_date    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unit_of_measure  VARCHAR(40),
    manufacturer_id  BIGINT REFERENCES Organization (id),
    price            BIGINT    NOT NULL CHECK (price > 0),
    manufacture_cost INTEGER,
    rating           BIGINT CHECK (rating > 0),
    part_number      VARCHAR(44) CHECK (length(part_number) >= 25 AND length(part_number) <= 44),
    owner_id         INTEGER   NOT NULL REFERENCES Person (id),
    user_id          BIGINT    NOT NULL REFERENCES users_is (id) -- Внешний ключ для пользователя
);
