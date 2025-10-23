package com.sprint.mission.discodeit.dto.message;

import java.util.List;
import java.util.UUID;

public record MessageRequestDto (
    String content,
    UUID channelId,
    UUID authorId,
    List<UUID> attachmentId
){
    public static MessageRequestDto create(String content, UUID channelId, UUID authorId, List<UUID> attachmentId) {
        return new MessageRequestDto(content, channelId, authorId, attachmentId);
    }

}
