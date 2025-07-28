-- 1) binary_contents 테이블
CREATE TABLE binary_contents (
                                 id           UUID                       PRIMARY KEY,
                                 created_at   TIMESTAMP WITH TIME ZONE   NOT NULL,
                                 file_name    VARCHAR(255)               NOT NULL,
                                 size         BIGINT                     NOT NULL,
                                 content_type VARCHAR(100)               NOT NULL,
                                 bytes        BYTEA                      NOT NULL
);

-- 2) users 테이블
CREATE TABLE users (
                       id           UUID                       PRIMARY KEY,
                       created_at   TIMESTAMP WITH TIME ZONE   NOT NULL,
                       updated_at   TIMESTAMP WITH TIME ZONE,
                       username     VARCHAR(50)                NOT NULL UNIQUE,
                       email        VARCHAR(100)               NOT NULL UNIQUE,
                       password     VARCHAR(60)                NOT NULL,
                       profile_id   UUID,
                       FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id) ON DELETE SET NULL
);

-- 3) channels 테이블
CREATE TABLE channels (
                          id           UUID       PRIMARY KEY,
                          created_at   TIMESTAMP WITH TIME ZONE   NOT NULL,
                          updated_at   TIMESTAMP WITH TIME ZONE,
                          name         VARCHAR(100),
                          description  VARCHAR(500),
                          type         VARCHAR(10)    NOT NULL CHECK (type IN ('PUBLIC','PRIVATE'))
);

-- 4) user_statuses 테이블
CREATE TABLE user_statuses (
                               id             UUID                 PRIMARY KEY,
                               created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
                               updated_at     TIMESTAMP WITH TIME ZONE,
                               user_id        UUID             NOT NULL UNIQUE,
                               last_active_at TIMESTAMP WITH TIME ZONE NOT NULL,
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE
);

-- 5) messages 테이블
CREATE TABLE messages (
                          id           UUID       PRIMARY KEY,
                          created_at   TIMESTAMP WITH TIME ZONE   NOT NULL,
                          updated_at   TIMESTAMP WITH TIME ZONE,
                          content      TEXT,
                          channel_id   UUID       NOT NULL,
                          author_id    UUID,

                          FOREIGN KEY (channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,

                          FOREIGN KEY (author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

-- 6) read_statuses 테이블
CREATE TABLE read_statuses (
                               id            UUID                     PRIMARY KEY,
                               created_at    TIMESTAMP WITH TIME ZONE NOT NULL,
                               updated_at    TIMESTAMP WITH TIME ZONE,
                               user_id       UUID                     NOT NULL,
                               channel_id    UUID                     NOT NULL,
                               last_read_at  TIMESTAMP WITH TIME ZONE NOT NULL,

                               UNIQUE (user_id, channel_id),  -- UK(user_id, channel_id)
                               FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               FOREIGN KEY (channel_id)
                                   REFERENCES channels(id)
                                   ON DELETE CASCADE
);

-- 7) message_attachments 테이블
CREATE TABLE message_attachments (
                                     message_id    UUID,
                                     attachment_id UUID,

                                     PRIMARY KEY (message_id, attachment_id),  -- 복합 PK

                                     FOREIGN KEY (message_id)
                                         REFERENCES messages(id)
                                         ON DELETE CASCADE,
                                     FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id)
                                         ON DELETE CASCADE
);
