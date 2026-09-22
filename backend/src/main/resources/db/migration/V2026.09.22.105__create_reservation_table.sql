CREATE TABLE reservations (

    id BIGSERIAL PRIMARY KEY,

    resource_id BIGINT NOT NULL,
    venue_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL,

    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,

    status VARCHAR(50) NOT NULL,
    max_participants INTEGER NOT NULL,

    CONSTRAINT fk_reservations_resource FOREIGN KEY (resource_id) REFERENCES resources(id),
    CONSTRAINT fk_reservations_venue FOREIGN KEY (venue_id) REFERENCES venue(id),
    CONSTRAINT fk_reservations_organizer FOREIGN KEY (organizer_id) REFERENCES users(id),
    CONSTRAINT chk_reservations_time CHECK (end_time > start_time),
    CONSTRAINT chk_reservations_max_participants CHECK (max_participants > 0)
);