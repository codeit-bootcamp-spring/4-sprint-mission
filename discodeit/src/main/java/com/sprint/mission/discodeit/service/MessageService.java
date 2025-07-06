package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateDto dto);
    List<MessageResponseDto> findAllByChannelId(UUID channelId);
    MessageResponseDto update(UUID messageId, MessageUpdateDto dto);
    void delete(UUID messageId);
}
