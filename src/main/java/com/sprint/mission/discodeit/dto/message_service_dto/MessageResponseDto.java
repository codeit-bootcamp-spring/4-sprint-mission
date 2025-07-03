package com.sprint.mission.discodeit.dto.message_service_dto;

import com.sprint.mission.discodeit.entity.Message;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
public class MessageResponseDto {

    private final UUID messageId;
    private final String messageContents;
    private final UUID authorId;
    private final UUID channelId;
    private final List<UUID> binaryContentIds;

    private final Instant createdAt;
    private final Instant updatedAt;

    public MessageResponseDto(Message message) {
        this.messageId = message.getId();
        this.messageContents = message.getMessageContents();
        this.authorId = message.getAuthorId();
        this.channelId = message.getChannelId();
        this.binaryContentIds = message.getBinaryContentIds();
        this.createdAt = message.getCreatedAt();
        this.updatedAt = message.getUpdatedAt();
    }
}
