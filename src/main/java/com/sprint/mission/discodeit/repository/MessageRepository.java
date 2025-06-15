package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message); // 저장 또는 수정

    void delete(UUID messageId);

    Message findById(UUID messageId);

    List<Message> findByBody(String messageBody);

    List<Message> findAll();

    boolean isContains(UUID messageId);

}
