CREATE TABLE participations
(
    id             BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT      NOT NULL,
    user_id        BIGINT,
    invited_email  VARCHAR(255),
    status         VARCHAR(20) NOT NULL DEFAULT 'INVITED',
    CONSTRAINT fk_participation_user foreign key (user_id) references users(id) on delete cascade,
    CONSTRAINT chk_participation_user_xor_email check (
        (user_id is not null and invited_email is null) or
        (user_id is null and invited_email is not null)
        )
);