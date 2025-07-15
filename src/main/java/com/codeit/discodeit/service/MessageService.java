package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.codeit.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO);

  List<Message> findMessagesByChannelId(UUID channelId);

  Message updateMessage(MessageUpdateRequestDto messageUpdateRequestDto);

  Message createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments);

}
