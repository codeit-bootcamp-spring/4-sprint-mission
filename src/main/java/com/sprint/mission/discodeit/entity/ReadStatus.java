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
@NoArgsConstructor(force=true)
public class ReadStatus {
    // 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
    // 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용한다

    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelID;

    public ReadStatus(UUID id, UUID userId, UUID channelID) {
        this.id = id;
        this.createdAt = Instant.now();
        this.userId = userId;
        this.channelID = channelID;
    }

}
