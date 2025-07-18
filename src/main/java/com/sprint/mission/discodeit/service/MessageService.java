package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.Message;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    com.sprint.mission.discodeit.entity.Message create(MessageCreateRequest messageCreateRequest, List<BinaryContentCreateRequest> binaryContentCreateRequests);
    com.sprint.mission.discodeit.entity.Message find(UUID messageId);
    List<com.sprint.mission.discodeit.entity.Message> findAllByChannelId(UUID channelId);
    com.sprint.mission.discodeit.entity.Message update(UUID messageId, MessageUpdateRequest request);
    void delete(UUID messageId);
    Message makeDto(com.sprint.mission.discodeit.entity.Message message);
}
