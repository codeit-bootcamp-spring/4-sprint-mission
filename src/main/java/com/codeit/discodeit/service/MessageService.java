package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.dto.response.PageResponse;
import com.codeit.discodeit.dto.response.Pageable;
import com.codeit.discodeit.entity.Message;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {

  Message createMessage(Message message, List<byte[]> attachmentsBytes) throws IOException;

  void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO);

  Message updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest);
  PageResponse<Message> findMessagesPerPage(UUID channelId, Pageable pageable);

  Optional<Message> findLastMessageInChannel(UUID channelId);

  Message findMessageByMessageId(UUID messageId);
}
