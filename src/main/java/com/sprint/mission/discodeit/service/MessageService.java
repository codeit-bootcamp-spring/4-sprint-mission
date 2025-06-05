package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    // 등록
    Message sendMessage(User sender, Channel channel, String content);

    // 단건 조회
    Message findMessage(UUID id);

    // 다건 조회
    List<Message> findAllMessages();

    // 수정
    Message updateMessage(UUID id, String newContent);

    // 삭제
    Message deleteMessage(UUID id);
}
