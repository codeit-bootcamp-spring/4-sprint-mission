package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant readAt;

    private UUID id;
    private UUID userId;
    private UUID channelId;

    public ReadStatus(UUID userId, UUID channelId, Instant readAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.readAt = null;
    }
}
