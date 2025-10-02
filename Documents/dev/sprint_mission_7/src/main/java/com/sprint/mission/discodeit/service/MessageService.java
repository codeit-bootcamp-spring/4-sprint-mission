package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(MessageCreateDto dto);
    MessageDto find(UUID messageId);
    Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);
    MessageDto update(MessageUpdateDto dto);
    void delete(UUID messageId);
}
