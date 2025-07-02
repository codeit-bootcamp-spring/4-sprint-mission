package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageMapper() {}

    public Message toEntity(MessageCreateDto dto){

        return new Message(
                dto.getContent(),
                dto.getChannelId(),
                dto.getAuthorId(),
                dto.getAttachmentIds()
        );
    }

    public MessageResponseDto toDto(Message message){

        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getChannelId(),
                message.getAuthorId(),
                message.getAttachmentIds()
        );
    }
}
