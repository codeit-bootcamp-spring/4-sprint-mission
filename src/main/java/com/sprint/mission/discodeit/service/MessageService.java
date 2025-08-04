package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(MessageCreateRequest messageCreateRequest, List<UUID> attachments);

    Page<Message> findAllByChannelId(UUID channelId, Instant cursor, Pageable pageable);

    Message updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest);

    void removeMessage(UUID messageId);
}
