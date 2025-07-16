package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.MessageEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

  MessageEntity save(MessageEntity messageEntity);

  List<MessageEntity> findAll();

  Optional<MessageEntity> findById(UUID id);

  void delete(UUID id);

  List<MessageEntity> findMessagesByContentContains(String MessageName);
}
