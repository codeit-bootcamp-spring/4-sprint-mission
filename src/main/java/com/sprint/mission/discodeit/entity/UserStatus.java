package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
* 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다.<br>
* 사용자의 온라인 상태를 확인하기 위해 활용합니다.
* */
@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant lastConnectedAt;

    private UUID userId;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastConnectedAt = Instant.now();
        this.userId = userId;
    }

    public void updateLastConnectedAt() {
        this.lastConnectedAt = Instant.now();
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }

    /*
    * 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드
    * */
    public boolean isOnline() {
        // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주
        return lastConnectedAt != null &&
                lastConnectedAt.isBefore(Instant.now().plusSeconds(300));
    }
}
