package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto.*;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface MessageService {

  MessageResponse create(MessageCreateRequest messageCreateRequest,
      List<MultipartFile> attachments);

  MessageResponse find(UUID messageId);

  List<MessageResponse> findallByChannelId(UUID channelId);

  MessageResponse update(UUID messageId, MessageUpdateRequestDto messageUpdateRequestDto);

  void delete(UUID id);

  void deleteAll();
}
