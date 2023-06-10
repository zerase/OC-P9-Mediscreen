-- Erase database (Note : Use following line below only in development phase)
DROP DATABASE IF EXISTS p9_mediscreen_dev;


-- Create database
CREATE DATABASE IF NOT EXISTS p9_mediscreen_dev;
USE p9_mediscreen_dev;


-- Create tables
CREATE TABLE IF NOT EXISTS patient (
    id            INT NOT NULL AUTO_INCREMENT,
    last_name     VARCHAR(30)  NOT NULL,
    first_name    VARCHAR(30)  NOT NULL,
    date_of_birth VARCHAR(10)  NOT NULL,
    gender        CHAR(1)      NOT NULL,
    address       VARCHAR(100) DEFAULT NULL,
    phone_number  VARCHAR(20)  DEFAULT NULL,

    PRIMARY KEY (id)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 DEFAULT COLLATE=utf8mb4_unicode_ci;
