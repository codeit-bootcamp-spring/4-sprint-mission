package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force=true)
public class ReadStatus {
    // 사용자가 채널 별 마지막으로 메시지를 읽은 시간을 표현하는 도메인 모델
    // 사용자별 각 채널에 읽지 않은 메시지를 확인하기 위해 활용한다

    private final UUID id;
    private Instant lastReadAt; // 마지막으로 메시지를 읽은 시간

    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private final UUID channelId;

    public ReadStatus(UUID id, UUID userId, UUID channelId, Instant lastReadAt) {
        this.id = UUID.randomUUID();
        this.lastReadAt = lastReadAt;
        this.createdAt = Instant.now();

        this.userId = userId;
        this.channelId = channelId;
    }

    public void update(Instant newLastReadAt) {
        newLastReadAt = Instant.now();
        if(newLastReadAt.isBefore(lastReadAt)) {
            lastReadAt = newLastReadAt;
            this.updatedAt = Instant.now();
        }
    }

}
