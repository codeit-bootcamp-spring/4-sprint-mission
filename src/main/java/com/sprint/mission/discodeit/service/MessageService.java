package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageResponseDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    public MessageResponseDto createMessage(MessageCreateRequest messageCreateRequest);
    public Message searchMessage(UUID id);
    public List<Message> searchAll();
    public MessageResponseDto updateMessage(UUID id, MessageUpdateRequest messageUpdateRequest);
    public void deleteMessage(UUID id);
}
