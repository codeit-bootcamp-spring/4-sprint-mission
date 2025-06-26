package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class UserStatus extends BaseEntity {
    private UUID userId;
    private Instant lastAccessAt;

    public UserStatus(UUID userId) {
        this.userId = userId;
        this.lastAccessAt = Instant.now();
    }

    public void updateAccessTime() {
        this.lastAccessAt = Instant.now();
        setUpdatedAt();
    }

    public boolean isOnline() {
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastAccessAt.isAfter(fiveMinutesAgo);
    }
}
