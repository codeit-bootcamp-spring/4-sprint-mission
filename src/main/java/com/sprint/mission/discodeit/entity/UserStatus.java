package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private LoginType loginType;

    private Instant createdAt;
    private Instant updatedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.loginType = LoginType.ONLINE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        return updatedAt.isAfter(Instant.now().minusSeconds(300));
    }

    public void setLoginType(LoginType loginType) {
        this.loginType = loginType;
        this.updatedAt = Instant.now();
    }

    public void update(LoginType loginType) {
        this.loginType = loginType;
        this.updatedAt = Instant.now();
    }
}
