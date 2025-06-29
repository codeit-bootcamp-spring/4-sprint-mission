package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    public Message MessageCreateDtoToMessage(MessageCreateDto dto) {
        return new Message(
                dto.getContent(),
                dto.getChannelId(),
                dto.getUserId()
        );
    }

    public MessageResponseDto MessageToMessageResponseDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getBinaryIds(),
                message.getCreatedAt()
        );
    }
}
