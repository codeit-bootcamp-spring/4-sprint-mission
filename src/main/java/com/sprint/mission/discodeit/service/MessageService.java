package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateDto messageCreateDto);

    MessageResponseDto find(UUID messageId);

    List<MessageResponseDto> findAllByChannelId(UUID channelId);

    MessageResponseDto update(MessageUpdateDto messageUpdateDto);

    void delete(UUID messageId);
}
