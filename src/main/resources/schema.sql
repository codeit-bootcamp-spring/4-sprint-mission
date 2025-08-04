create table binary_contents
(
    id           uuid                     default uuid_generate_v4() not null
        primary key,
    created_at   timestamp with time zone default CURRENT_TIMESTAMP  not null,
    file_name    varchar(255)                                        not null,
    size         bigint                                              not null,
    content_type varchar(100)                                        not null
);

alter table binary_contents
    owner to discodeit_user;

create table users
(
    id         uuid                     default uuid_generate_v4() not null
        primary key,
    created_at timestamp with time zone default CURRENT_TIMESTAMP  not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    username   varchar(50)                                         not null
        unique,
    email      varchar(100)                                        not null
        unique,
    password   varchar(60)                                         not null,
    profile_id uuid
                                                                   references binary_contents
                                                                       on delete set null
);

alter table users
    owner to discodeit_user;

create table user_statuses
(
    id             uuid                     default uuid_generate_v4() not null
        primary key,
    created_at     timestamp with time zone default CURRENT_TIMESTAMP  not null,
    updated_at     timestamp with time zone default CURRENT_TIMESTAMP,
    user_id        uuid                                                not null
        unique
        references users
            on delete cascade,
    last_active_at timestamp with time zone default CURRENT_TIMESTAMP  not null
);

alter table user_statuses
    owner to discodeit_user;

create table channels
(
    id          uuid                     default uuid_generate_v4() not null
        primary key,
    created_at  timestamp with time zone default CURRENT_TIMESTAMP  not null,
    updated_at  timestamp with time zone default CURRENT_TIMESTAMP,
    name        varchar(100),
    description varchar(500),
    type        varchar(10)                                         not null
        constraint channels_type_check
            check ((type)::text = ANY ((ARRAY ['PUBLIC'::character varying, 'PRIVATE'::character varying])::text[]))
);

alter table channels
    owner to discodeit_user;

create table read_statuses
(
    id           uuid                     default uuid_generate_v4() not null
        primary key,
    created_at   timestamp with time zone default CURRENT_TIMESTAMP  not null,
    updated_at   timestamp                default CURRENT_TIMESTAMP,
    user_id      uuid                                                not null
        references users
            on delete cascade,
    channel_id   uuid                                                not null
        references channels
            on delete cascade,
    last_read_at timestamp with time zone default CURRENT_TIMESTAMP  not null,
    constraint read_statuses_user_id_channel_id_unique
        unique (user_id, channel_id)
);

alter table read_statuses
    owner to discodeit_user;

create table messages
(
    id         uuid                     default uuid_generate_v4() not null
        primary key,
    created_at timestamp with time zone default CURRENT_TIMESTAMP  not null,
    updated_at timestamp with time zone default CURRENT_TIMESTAMP,
    content    text,
    channel_id uuid                                                not null
        references channels
            on delete cascade,
    author_id  uuid
                                                                   references users
                                                                       on delete set null
);

alter table messages
    owner to discodeit_user;

create table message_attachments
(
    message_id    uuid
        references messages
            on delete cascade,
    attachment_id uuid
        references binary_contents
            on delete cascade
);

alter table message_attachments
    owner to discodeit_user;


