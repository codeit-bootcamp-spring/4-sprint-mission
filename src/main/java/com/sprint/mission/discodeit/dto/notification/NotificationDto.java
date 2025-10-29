package com.sprint.mission.discodeit.dto.notification;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationDto {
    private UUID id;
    private UUID receiverId;
    private String title;
    private String content;
    private Instant createdAt;
}
