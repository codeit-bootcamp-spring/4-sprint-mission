package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateServiceRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MessageService {

    MessageDto createMessage(MessageCreateServiceRequest messageCreateServiceRequest);

    public MessageDto updateMessage(UUID messageId, MessageUpdateServiceRequest request);

    void deleteMessage(UUID messageId);

    public Page<MessageDto> findAllByChannelId(UUID channelId, Pageable pageable);
}
