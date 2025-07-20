package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastAccessedAt;

    private UUID id;
    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = id;
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.lastAccessedAt = this.updatedAt;
    }

    public boolean isOnline() {
        return Duration.between(lastAccessedAt, Instant.now()).toMinutes() >= 5;
    }
}


