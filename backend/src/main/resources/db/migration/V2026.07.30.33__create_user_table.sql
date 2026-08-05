CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    phone         VARCHAR(50) NOT NULL,
    role          VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP(6) NOT NULL,

    CONSTRAINT chk_users_email_not_empty CHECK (length(trim(email)) > 0),
    CONSTRAINT chk_users_role_not_empty CHECK (length(trim(role)) > 0),
    CONSTRAINT chk_users_phone_not_empty CHECK (phone IS NULL OR length(trim(phone)) > 0)
);