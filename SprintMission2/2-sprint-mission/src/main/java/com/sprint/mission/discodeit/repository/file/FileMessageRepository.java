package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageState;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {
  private static final String DATA_FILE = "data/message.ser";
  private final Map<UUID, Message> messageCache = new ConcurrentHashMap<>();

  public FileMessageRepository() {
    loadAllMessagesToCache();
  }

  public void saveAllMessageToFile() {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
      List<Message> messages = new ArrayList<>(messageCache.values());
      oos.writeObject(messages);
    } catch (IOException e) {
      throw new RuntimeException("저장 오류: " + e);
    }
  }

  @SuppressWarnings("unchecked")
  public void loadAllMessagesToCache() {
    File file = new File(DATA_FILE);
    if (!file.exists()) {
      return;
    }
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
      List<Message> messages = (List<Message>) ois.readObject();
      messages.forEach(
          message -> {
            messageCache.put(message.getMessageId(), message);
          });
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("불러오기 오류: " + e.getMessage());
    }
  }

  @Override
  public Message save(Message message) {
    messageCache.put(message.getMessageId(), message);
    saveAllMessageToFile();
    return message;
  }

  @Override
  public List<Message> findAll() {
    return messageCache.values().stream()
        .filter(message -> message.getState() != MessageState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Message> findById(UUID id) {
    Message message = messageCache.get(id);
    if (message == null || message.getState() == MessageState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(message);
  }

  @Override
  public void delete(Message message) {
    Message cached = messageCache.get(message.getMessageId());
    if (cached != null && cached.getState() != MessageState.DELETED) {
      cached.deletedMessageState();
      saveAllMessageToFile();
    }
  }

  @Override
  public List<Message> findMessagesByContentContains(String MessageName) {
    return messageCache.values().stream()
        .filter(message -> message.getState() != MessageState.DELETED)
        .filter(message -> message.getContent().contains(MessageName))
        .collect(Collectors.toList());
  }
}
