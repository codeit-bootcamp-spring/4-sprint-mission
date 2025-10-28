CREATE TABLE users(
                      id uuid primary key ,
                      created_at timestamptz NOT NULL,
                      updated_at timestamptz,
                      username varchar(50) UNIQUE NOT NULL,
                      email varchar(100) UNIQUE NOT NULL,
                      password varchar(60) NOT NULL
);

ALTER TABLE users add profile_id uuid;

ALTER TABLE users
    ADD CONSTRAINT fk_user_profile_id
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents(id)
            ON DELETE SET NULL;

CREATE TABLE user_statuses(
                              id uuid primary key ,
                              created_at timestamptz not null ,
                              updated_at timestamptz,
                              user_id uuid UNIQUE NOT NULL,
                              last_active_at timestamptz NOT NULL,
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE binary_contents (
                                 id uuid PRIMARY KEY ,
                                 created_at timestamptz NOT NULL,
                                 file_name varchar(255) NOT NULL,
                                 size bigint NOT NULL,
                                 content_type varchar(100) NOT NULL,
                                 bytes bytea NOT NULL
);

CREATE TYPE channel_type AS ENUM ('PUBLIC', 'PRIVATE');

CREATE TABLE channels(
                         id uuid PRIMARY KEY ,
                         created_at timestamptz NOT NULL,
                         updated_at timestamptz,
                         name varchar(100),
                         description varchar(500),
                         type varchar(10) NOT NULL CHECK ( type IN ('PUBLIC','PRIVATE') )
);

CREATE TABLE read_statuses(
                              id uuid PRIMARY KEY,
                              created_at timestamptz NOT NULL,
                              updated_at timestamptz,
                              user_id uuid ,
                              channel_id uuid ,
                              last_read_at timestamptz NOT NULL,
                              CONSTRAINT uk_user_channel UNIQUE (user_id, channel_id),
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE
);

CREATE TABLE messages (
                          id uuid PRIMARY KEY ,
                          created_at timestamptz NOT NULL,
                          updated_at timestamptz,
                          content text,
                          channel_id uuid NOT NULL,
                          author_id uuid,
                          FOREIGN KEY (channel_id) REFERENCES channels(id) ON DELETE CASCADE,
                          FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE message_attachments(
                                    message_id uuid,
                                    attachment_id uuid,
                                    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,
                                    FOREIGN KEY (attachment_id) REFERENCES binary_contents(id) ON DELETE CASCADE
);

CREATE TABLE persistent_logins (
                                   username VARCHAR(64) NOT NULL,
                                   series VARCHAR(64) PRIMARY KEY,
                                   token VARCHAR(64) NOT NULL,
                                   last_used TIMESTAMP NOT NULL
);