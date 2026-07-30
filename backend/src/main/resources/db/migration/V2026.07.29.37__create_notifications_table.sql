create table notifications(
    id bigint primary key generated always as identity,
    recipient_id bigint not null,
    reservation_id bigint,
    type varchar(100) not null,
    message varchar(500) not null,
    sent_at timestamp not null,
    read boolean not null default false,
    constraint fk_recipient foreign key (recipient_id) references users(id) on delete cascade
)