package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MessageMapper {

    public Message messageCreateDtoToMessage(MessageCreateDto dto) {
        return new Message(
                dto.authorId(),
                dto.channelId(),
                dto.content()
        );
    }

    public MessageResponseDto messageToMessageResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getAttachmentIds()
        );
    }

}
