package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class ReadStatus extends BaseEntity {
    private final UUID id;
    private final UUID userId;
    private final UUID channelId;
    private Instant readTime;

    public ReadStatus(UUID userId, UUID channelId) {
        super();
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.channelId = channelId;
        this.readTime = Instant.now();
    }

    public void updateReadTime() {
        this.readTime = Instant.now();
    }
}
