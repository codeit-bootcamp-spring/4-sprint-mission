package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageResponseDto {
    private UUID messageId;
    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentId;
    private Instant createdAt;
}