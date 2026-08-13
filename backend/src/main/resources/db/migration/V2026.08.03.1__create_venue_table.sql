CREATE TABLE venue
(
    id          BIGSERIAL PRIMARY KEY,
    managed_by  BIGINT NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    address     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_venue_managed_by FOREIGN KEY (managed_by) REFERENCES users (id)
);
