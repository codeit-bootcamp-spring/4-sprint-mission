package com.sprint.mission.discodeit.maper;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.response.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MessageMapper {
    public Message messageCreateRequestToMessage(MessageCreateRequest dto, List<UUID> attachmentIds) {
        return new Message(
                dto.content(),
                dto.channelId(),
                dto.authorId(),
                attachmentIds
        );
    }

    public MessageResponseDto messageToMessageResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}
