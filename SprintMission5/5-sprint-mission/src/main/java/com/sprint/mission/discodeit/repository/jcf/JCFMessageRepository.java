package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {

  private final Map<UUID, MessageEntity> data = new ConcurrentHashMap<>();

  @Override
  public MessageEntity save(MessageEntity messageEntity) {
    data.put(messageEntity.getId(), messageEntity);
    return messageEntity;
  }

  @Override
  public List<MessageEntity> findAll() {
    return data.values().stream()
        .filter(message -> message.getState() != MessageState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<MessageEntity> findById(UUID id) {
    MessageEntity messageEntity = data.get(id);
    if (messageEntity == null || messageEntity.getState() == MessageState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(messageEntity);
  }

  @Override
  public void delete(UUID id) {
    MessageEntity existing = data.get(id);
    if (existing != null && existing.getState() != MessageState.DELETED) {
      existing.setToDeleted();
    }
  }

  @Override
  public List<MessageEntity> findMessagesByContentContains(String MessageName) {
    return data.values().stream()
        .filter(message -> message.getState() != MessageState.DELETED)
        .filter(message -> message.getContents().contains(MessageName))
        .collect(Collectors.toList());
  }
}
