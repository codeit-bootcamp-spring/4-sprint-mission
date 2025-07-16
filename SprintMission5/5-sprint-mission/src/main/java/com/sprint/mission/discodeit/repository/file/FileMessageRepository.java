package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.MessageEntity;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileMessageRepository implements MessageRepository {

  private final Map<UUID, MessageEntity> messageCache = new ConcurrentHashMap<>();
  private final File messageDir;

  public FileMessageRepository(String baseDirectory) {
    this.messageDir = new File(baseDirectory, "Message");
    if (!messageDir.exists()) {
      if (!messageDir.mkdirs()) {
        throw new RuntimeException("디렉토리 생성 실패: " + messageDir.getAbsolutePath());
      }
    }
    loadAllMessagesToCache();
  }

  private void loadAllMessagesToCache() {
    File[] files = messageDir.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files != null) {
      for (File file : files) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
          MessageEntity message = (MessageEntity) ois.readObject();
          messageCache.put(message.getId(), message);
        } catch (IOException | ClassNotFoundException e) {
          System.err.println("메시지 로딩 실패: " + file.getName() + " - " + e.getMessage());
        }
      }
    }
  }

  private void saveMessageToFile(MessageEntity message) {
    File file = new File(messageDir, message.getId() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException("메시지 저장 실패: " + file.getAbsolutePath(), e);
    }
  }

  @Override
  public MessageEntity save(MessageEntity messageEntity) {
    messageCache.put(messageEntity.getId(), messageEntity);
    saveMessageToFile(messageEntity);
    return messageEntity;
  }

  @Override
  public List<MessageEntity> findAll() {
    return messageCache.values().stream()
        .filter(m -> m.getState() != MessageState.DELETED)
        .toList();
  }

  @Override
  public Optional<MessageEntity> findById(UUID id) {
    MessageEntity message = messageCache.get(id);
    if (message == null || message.getState() == MessageState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(message);
  }

  @Override
  public void delete(UUID id) {
    MessageEntity cached = messageCache.get(id);
    if (cached != null && cached.getState() != MessageState.DELETED) {
      cached.setToDeleted();
      saveMessageToFile(cached);
    }
  }

  @Override
  public List<MessageEntity> findMessagesByContentContains(String content) {
    return messageCache.values().stream()
        .filter(m -> m.getState() != MessageState.DELETED)
        .filter(m -> m.getContents() != null && m.getContents().contains(content))
        .toList();
  }
}