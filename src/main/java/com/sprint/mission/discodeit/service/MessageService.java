package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    MessageResponseDto createMessage(MessageCreateRequestDto messageCreateRequestDTO);
    void deleteMessage(DeleteMessageRequestDto deleteMessageRequestDTO);


    List<MessageResponseDto> findAllMessage();
    List<MessageResponseDto> findMessagesByChannelId(UUID channelId);
    MessageResponseDto updateMessage(MessageUpdateRequestDto messageUpdateRequestDTO);

}
