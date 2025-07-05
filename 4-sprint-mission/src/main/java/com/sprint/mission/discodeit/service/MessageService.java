package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    public MessageResponseDto createMessage(MessageCreateDto messageCreateDto); // 메세지 입력

    public List<MessageResponseDto> findAllByChannelId(UUID channelId); // 모든 메세지 출력

    public MessageResponseDto findMessageById(UUID messageId); // 특정 메세지 출력

    public List<MessageResponseDto> findMessagesByContentContains(
            String MessageName); // 특정 단어가 포함된 메세지 출력

    public MessageResponseDto updateMessage(MessageUpdateDto messageUpdateDto); // 메세지 수정

    public void deleteMessage(UUID messageId); // 메세지 삭제
}
