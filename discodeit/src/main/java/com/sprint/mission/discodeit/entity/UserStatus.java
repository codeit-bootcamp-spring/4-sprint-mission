package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private Instant lastActiveAt;
    @Setter
    private UserState userState;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
        this.lastActiveAt = Instant.now();
    }

    public enum UserState {
        ONLINE, OFFLINE
    }

    public void updateLastActiveAt(Instant newActiveAt) {
        this.lastActiveAt = newActiveAt != null ? newActiveAt : Instant.now();
        this.userState = isOnline() ? UserState.ONLINE : UserState.OFFLINE;
        this.updatedAt = Instant.now();
    }

    public boolean isOnline() {
        // 온라인 판별 기준 예시 (5분 이내 활동)
        return lastActiveAt != null && lastActiveAt.isAfter(Instant.now().minusSeconds(300));
    }
}
