package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest dto, List<MultipartFile> attachments);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(UUID messageId, MessageUpdateRequestDto dto);
    void delete(UUID messageId);
}
