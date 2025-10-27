CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       created_at timestamp with time zone NOT NULL,
                       updated_at timestamp with time zone,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password VARCHAR(60) NOT NULL,
                       profile_id UUID,
                       CONSTRAINT fk_profile FOREIGN KEY (profile_id)
                           REFERENCES binary_contents(id)
                           ON DELETE SET NULL
);

CREATE TABLE binary_contents  (
                                  id UUID PRIMARY KEY,
                                  created_at timestamp with time zone NOT NULL,
                                  file_name VARCHAR(255) NOT NULL,
                                  size BIGINT NOT NULL,
                                  content_type VARCHAR(100) NOT NULL,
                                  bytes BYTEA NOT NULL
);

CREATE TABLE channels (
                          id UUID PRIMARY KEY,
                          created_at timestamp with time zone NOT NULL,
                          updated_at timestamp with time zone,
                          name VARCHAR(100),
                          description VARCHAR(500),
                          type VARCHAR(10) NOT NULL
);

CREATE TABLE messages (
                          id UUID PRIMARY KEY,
                          created_at timestamp with time zone NOT NULL,
                          updated_at timestamp with time zone,
                          content TEXT,
                          channel_id UUID NOT NULL,
                          author_id UUID,
                          CONSTRAINT fk_channel FOREIGN KEY (channel_id)
                              REFERENCES channels(id)
                              ON DELETE CASCADE,
                          CONSTRAINT fk_author FOREIGN KEY  (author_id)
                              REFERENCES users(id)
                              ON DELETE SET NULL
);

CREATE TABLE user_statuses (
                               id UUID PRIMARY KEY,
                               created_at timestamp with time zone NOT NULL,
                               updated_at timestamp with time zone,
                               user_id UUID UNIQUE NOT NULL,
                               CONSTRAINT fk_user_statuses FOREIGN KEY (user_id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               last_active_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE read_statuses (
                               id UUID PRIMARY KEY,
                               created_at timestamp with time zone NOT NULL,
                               updated_at timestamp with time zone,
                               user_Id UUID,
                               channel_Id UUID,
                               last_read_at TIMESTAMPTZ NOT NULL,

                               CONSTRAINT uq_user_channel UNIQUE (user_Id,channel_Id),
                               CONSTRAINT fk_read_user FOREIGN KEY (user_Id)
                                   REFERENCES users(id)
                                   ON DELETE CASCADE,
                               CONSTRAINT fk_read_channel FOREIGN KEY (channel_Id)
                                   REFERENCES channels(id)
                                   ON DELETE CASCADE
);

CREATE TABLE message_attachments (
                                     message_id uuid,
                                     attachment_id UUID,

                                     CONSTRAINT fk_attachment_message FOREIGN KEY (message_id)
                                         REFERENCES messages(id),
                                     CONSTRAINT fk_attachment FOREIGN KEY (attachment_id)
                                         REFERENCES binary_contents(id)
);

