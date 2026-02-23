CREATE TABLE user_table (
    id CHAR(36) NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);
