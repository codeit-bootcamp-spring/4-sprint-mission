package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.DTO.UserStatusRequest;
import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastSeenAt; // 마지막 접속 시간

    public UserStatus(UserStatusRequest request) {
        this.id = UUID.randomUUID();
        this.userId = request.userId();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.lastSeenAt = this.createdAt;
    }

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.lastSeenAt = this.createdAt;
    }

    public void updateLastSeen() {
        this.lastSeenAt = Instant.now();
        this.updatedAt = this.lastSeenAt;
    }

    public void updateLastSeenAt(Instant lastSeenAt) {
        this.updatedAt = this.lastSeenAt;
    }

    public boolean isOnline() {
        return lastSeenAt != null &&
                lastSeenAt.isAfter(Instant.now().minus(Duration.ofMinutes(5)));
       //lastSeenAt이 현재 시간에서 5분을 뺀 시간보다 나중이면 == 마지막 접속 시간이 5분 전 이후면 == 접속한 지 5분 이내
    }

}
