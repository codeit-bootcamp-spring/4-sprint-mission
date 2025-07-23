package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message toMessage(MessageCreateRequest messageCreateRequest) {
        return new Message(messageCreateRequest.authorId(), messageCreateRequest.channelId(), messageCreateRequest.content());
    }

    public MessageResponseDto toMessageResponseDto(Message message) {
        return new MessageResponseDto(message.getId(), message.getAuthorId(), message.getChannelId(), message.getContent(), message.getAttachmentIds(), message.getCreatedAt(), message.getUpdatedAt());
    }
}
