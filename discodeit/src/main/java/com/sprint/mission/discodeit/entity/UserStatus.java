package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private Instant lastActiveAt;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public void updateLastActiveAt() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String isOnline() {
        Instant now = Instant.now();
        if (lastActiveAt != null && now.minusSeconds(300).isBefore(lastActiveAt)) {
            return "online";
        } else {
            return "offline";
        }
    }
}
