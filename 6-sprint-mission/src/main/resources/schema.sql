CREATE TABLE IF NOT EXISTS binary_contents
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    file_name varchar(255),
    size bigint,
    content_type varchar(100),
    bytes bytea
);

CREATE TABLE IF NOT EXISTS users
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    username varchar(50) not null unique,
    email varchar(100) not null unique,
    password varchar(60) not null,
    profile_id uuid references binary_contents on delete set null
);

CREATE TABLE IF NOT EXISTS user_statuses
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    user_id uuid not null unique references users on delete cascade,
    last_active_at timestamp with time zone not null
);

CREATE TABLE IF NOT EXISTS channels
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    name varchar(100),
    description varchar(500),
    type varchar(10) not null constraint channels_type_check
        check ((type)::text = ANY
               ((ARRAY ['PUBLIC'::character varying, 'PRIVATE'::character varying])::text[]))
);

CREATE TABLE IF NOT EXISTS read_statuses
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    user_id uuid references users on delete cascade,
    channel_id uuid references channels on delete cascade,
    last_read_at timestamp with time zone not null,
    unique (user_id, channel_id)
);

CREATE TABLE IF NOT EXISTS messages
(
    id uuid not null primary key,
    created_at timestamp with time zone not null,
    updated_at timestamp with time zone,
    content text,
    channel_id uuid not null references channels on delete cascade,
    author_id  uuid references users on delete set null
);

CREATE TABLE IF NOT EXISTS message_attachments
(
    message_id uuid references messages on delete cascade,
    attachment_id uuid references binary_contents on delete cascade,
    primary key (message_id, attachment_id)
);
