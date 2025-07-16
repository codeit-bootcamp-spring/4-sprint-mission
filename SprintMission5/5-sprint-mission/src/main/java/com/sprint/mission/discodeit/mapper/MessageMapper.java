package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.message.*;
import com.sprint.mission.discodeit.entity.MessageEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Component
public class MessageMapper {

//  public MessageEntity messageCreateDtoToMessage(MessageCreateRequest messageCreateRequest) {
//    return new MessageEntity(
//        messageCreateRequest.content(),
//        messageCreateRequest.authorId(),
//        messageCreateRequest.channelId()
//    );
//  }

  public Message messageToMessageResponseDto(MessageEntity messageEntity) {
    return new Message(
        messageEntity.getId(),
        messageEntity.getCreatedAt(),
        messageEntity.getUpdatedAt(),
        messageEntity.getContents(),
        messageEntity.getChannelId(),
        messageEntity.getAuthorId(),
        messageEntity.getAttachmentIds()
    );
  }

  public MessageCreateServiceRequest toMessageCreateServiceRequest(
      MessageCreateRequest request,
      List<MultipartFile> attachments
  ) {
    return new MessageCreateServiceRequest(
        request.content(),
        request.channelId(),
        request.authorId(),
        attachments
    );
  }

  public MessageUpdateServiceRequest toMessageUpdateServiceRequest(
      MessageUpdateRequest request,
      List<MultipartFile> attachments
  ) {
    return new MessageUpdateServiceRequest(
        request.newContent(),
        attachments
    );
  }
}
