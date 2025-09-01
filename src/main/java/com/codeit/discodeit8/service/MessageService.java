package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit8.dto.message_service_dto.MessageDto;
import com.codeit.discodeit8.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit8.dto.response.PageResponse;
import com.codeit.discodeit8.dto.response.Pageable;
import com.codeit.discodeit8.entity.Message;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageDto createMessage(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachmentsList) throws IOException;

  void deleteMessage(UUID messageId);

  MessageDto updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest);

  PageResponse<MessageDto> findMessagesPerPage(UUID channelId, Pageable pageable);

  Message findMessageByMessageId(UUID messageId);
}
