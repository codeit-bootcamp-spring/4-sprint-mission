package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.MessageCreateRequest;
import com.sprint.mission.discodeit.DTO.MessageResponse;
import com.sprint.mission.discodeit.DTO.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request);
    MessageResponse find(UUID messageId);
    List<MessageResponse> findAll();
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(MessageUpdateRequest request);
    void delete(UUID messageId);
}
