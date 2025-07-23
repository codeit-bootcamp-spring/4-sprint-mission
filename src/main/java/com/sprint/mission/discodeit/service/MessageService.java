package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto createMessage(MessageCreateRequest messageCreateRequest, List<BinaryContentPostDto> attachments);

    List<MessageResponseDto> findallByChannelId(UUID channelId);

    MessageResponseDto getMessagesById(UUID messageId);

    MessageResponseDto updateMessage(UUID messageId, MessageUpdateRequest messageUpdateRequest);

    void removeMessage(UUID messageId);

    List<MessageResponseDto> getActiveMessages();

    List<MessageResponseDto> findAll();
}
