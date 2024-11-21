CREATE TABLE Coordinates (
                             id SERIAL PRIMARY KEY,
                             x INTEGER NOT NULL,
                             y REAL CHECK (y <= 327)
);

CREATE TABLE Location (
                          id SERIAL PRIMARY KEY,
                          x INTEGER NOT NULL,
                          y REAL NOT NULL,
                          z INTEGER NOT NULL,
                          name VARCHAR NOT NULL CHECK (name <> '')
);

CREATE TABLE Address (
                         id SERIAL PRIMARY KEY,
                         street VARCHAR NOT NULL,
                         zip_code VARCHAR NOT NULL
);

CREATE TYPE UnitOfMeasure AS ENUM ('CENTIMETERS', 'SQUARE_METERS', 'MILLILITERS');

CREATE TYPE OrganizationType AS ENUM (
    'COMMERCIAL',
    'PUBLIC',
    'TRUST',
    'PRIVATE_LIMITED_COMPANY',
    'OPEN_JOINT_STOCK_COMPANY'
);

CREATE TYPE Color AS ENUM ('BLACK', 'BLUE', 'WHITE', 'BROWN');

CREATE TYPE Country AS ENUM ('SPAIN', 'ITALY', 'NORTH_KOREA');

CREATE TABLE Organization (
                              id BIGSERIAL PRIMARY KEY,
                              name VARCHAR NOT NULL CHECK (name <> ''),
                              official_address_id INTEGER NOT NULL REFERENCES Address(id),
                              annual_turnover REAL NOT NULL CHECK (annual_turnover > 0),
                              employees_count INTEGER CHECK (employees_count > 0),
                              full_name VARCHAR(1668) UNIQUE NOT NULL,
                              type OrganizationType NOT NULL
);

CREATE TABLE Person (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR NOT NULL CHECK (name <> ''),
                        eye_color Color NOT NULL,
                        hair_color Color,
                        location_id INTEGER NOT NULL REFERENCES Location(id),
                        weight DOUBLE PRECISION NOT NULL CHECK (weight > 0),
                        nationality Country NOT NULL
);

CREATE TABLE Product (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR NOT NULL CHECK (name <> ''),
                         coordinates_id INTEGER NOT NULL REFERENCES Coordinates(id),
                         creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         unit_of_measure UnitOfMeasure,
                         manufacturer_id BIGINT REFERENCES Organization(id),
                         price BIGINT NOT NULL CHECK (price > 0),
                         manufacture_cost INTEGER,
                         rating BIGINT CHECK (rating > 0),
                         part_number VARCHAR(44) CHECK (length(part_number) >= 25 AND length(part_number) <= 44),
                         owner_id INTEGER NOT NULL REFERENCES Person(id)
);
