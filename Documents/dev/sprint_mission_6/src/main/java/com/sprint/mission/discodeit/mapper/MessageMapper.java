package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring", uses = {UserMapper.class, BinaryContentMapper.class})
public interface MessageMapper {

    default Message messageCreateDtoToMessage(MessageCreateDto dto){
        Message message = new Message();
        message.setContent(dto.getContent());
        return message;
    }

    @Mapping(source = "channel.id", target = "channelId")
    @Mapping(source = "attachments",target = "attachments")
    MessageDto messageToMessageDto(Message message);
}

//    public Message messageCreateDtoToMessage(MessageCreateDto dto) {
//        Message message = new Message(
//                dto.getContent(),
//                dto.getChannelId(),
//                dto.getAuthorId()
//                );
//        if (dto.getAttachmentId() != null) {
//            message.setAttachmentId(dto.getAttachmentId());
//        }
//        return message;
//    }

//    public MessageResponseDto messageToMessageResponseDto(Message message) {
//        return new MessageResponseDto(
//                message.getId(),
//                message.getContent(),
//                message.getChannelId(),
//                message.getAuthorId(),
//                message.getAttachmentId(),
//                message.getCreatedAt()
//        );
//    }
//}
