package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.entity.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record MessageResponseDto(
    UUID messageId,
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentId,
    Instant createdAt
) {
    public static MessageResponseDto from(Message message) {
        return new MessageResponseDto(message.getId(), message.getContent(), message.getChannelId(), message.getAuthorId(),message.getAttachmentId(), message.getCreatedAt());
    }
}
