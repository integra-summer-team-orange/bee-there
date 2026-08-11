CREATE TABLE resources
(
    id                   BIGSERIAL PRIMARY KEY,
    venue_id             BIGINT         NOT NULL,
    name                 VARCHAR(255)   NOT NULL,
    activity_type        VARCHAR(255)   NOT NULL,
    activity_description TEXT,
    capacity             INTEGER        NOT NULL,
    type                 VARCHAR(255)   NOT NULL,
    hourly_rate          NUMERIC(10, 2) NOT NULL,
    CONSTRAINT fk_resources_venue FOREIGN KEY (venue_id) REFERENCES venue (id) ON DELETE CASCADE,
    CONSTRAINT chk_resources_name_not_empty CHECK (length(trim(name)) > 0),
    CONSTRAINT chk_resources_activity_type_not_empty CHECK (length(trim(activity_type)) > 0),
    CONSTRAINT chk_resources_type_not_empty CHECK (length(trim(type)) > 0),
    CONSTRAINT chk_resources_capacity_positive CHECK (capacity > 0),
    CONSTRAINT chk_resources_hourly_rate_non_negative CHECK (hourly_rate >= 0)
);
