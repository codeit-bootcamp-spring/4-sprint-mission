package com.sprint.mission.discodeit.dto.data;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    Instant createdAt, // 메세지 생성된 시간
    UUID receiverId, // 알림을 수신할 User의 id
    String title,
    String content
) {

}
