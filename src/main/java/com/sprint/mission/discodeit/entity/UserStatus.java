package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UserStatus {
    // 사용자별 마지막으로 확인된 접속시간을 표현하는 도메인 모델
    // 사용자의 온라인 상태를 확인하기 위해 활용함
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;
    private boolean online = true;
    private boolean offline = false;

    private final UUID userId;
    public UserStatus(UUID id, UUID userId) {
        this.id = id;
        this.createdAt = Instant.now();
        this.userId = userId;
    }

    // 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드 정의
    // 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속중인 유저로 간주함
    public boolean nowLogin() {
        Instant now = Instant.now();
        if(now.getEpochSecond() - createdAt.getEpochSecond() <= 300) {
            return this.online;
        }
        return this.offline;
        // 프로필 이미지가 실제로 바이너리 컨텐츠이다
        // 이게 많이 쫌 어려울거다
        // CRUD 구현하고 create 구현하는걸 권장
    }
}
