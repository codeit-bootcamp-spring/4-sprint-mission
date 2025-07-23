package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserStatus extends BasedEntity {
    private UUID userId;
    private Instant lastActiveAt;
    private boolean online;

    public boolean isRecentlyActive() {
        if (lastActiveAt.isAfter(Instant.now().minusSeconds(300))) return true;

        return false;
    }

    @Override
    public String toString() {
        return "UserStatus{" +
                "userId=" + userId +
                ", lastActiveAt=" + lastActiveAt +
                ", online=" + online +
                '}';
    }

    public void update(Instant newLastActiveAt) {
        if (lastActiveAt.isAfter(newLastActiveAt)) {
            System.out.println("가지고 있는 데이터가 더 최신입니다.");
            return;
        }
        this.lastActiveAt = newLastActiveAt;
    }
}
