package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateDto messageCreateDto);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findallByChannelId(UUID channelId);
    MessageResponseDto update(MessageUpdateDto messageUpdateDto);
    void delete(UUID messageId);
}

