package com.codeit.discodeit.repository.file;

import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;


public class FileMessageRepository implements MessageRepository, Serializable {

  private final String filePath;

  public FileMessageRepository(String fileDirectory) {
    this.filePath = fileDirectory + "/message.ser";
  }

  @Override
  public List<Message> loadMessages() {
    File file = new File(filePath);

    if (!file.exists() || file.length() == 0) {
      return new ArrayList<Message>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
    }
    // ArrayList<User> 역직렬화및 반환
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
      return (List<Message>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  @Override
  public void saveMessages(List<Message> messages) {
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
      oos.writeObject(messages);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void createMessage(Message message) {
    //System.out.printf("메세지를 추가합니다. User: %s, ChannelId: %s, Contents: %s%n", user.getId(), channel.getId(), contents);
    List<Message> messages = loadMessages();
    messages.add(message);
    saveMessages(messages);
  }

  @Override
  public void deleteMessageByMessageId(UUID messageId) {
    List<Message> messagesFromFile = loadMessages();
    messagesFromFile.removeIf(msg -> msg.equalsId(messageId));
    saveMessages(messagesFromFile);
  }

  @Override
  public void deleteMessagesByChannelId(UUID channelId) {

    List<Message> messagesFromFile = loadMessages();
    messagesFromFile.removeIf(msg -> msg.getChannelId().equals(channelId));
    saveMessages(messagesFromFile);
  }

  @Override
  public void updateMessage(Message message) {

    List<Message> messagesFromFile = loadMessages();

    for (int i = 0; i < messagesFromFile.size(); i++) {
      if (messagesFromFile.get(i).equalsId(message)) {
        messagesFromFile.set(i, message);  // 리스트 내부 요소를 실제로 교체
        saveMessages(messagesFromFile);   // 변경된 리스트 저장
        return;
      }
    }
  }

  @Override
  public Optional<Message> findMessageByMessageId(UUID messageId) {
    return loadMessages().stream()
        .filter(msg -> msg.getId().equals(messageId))
        .findFirst();
  }

  @Override
  public List<Message> findMessagesByMessageIds(Set<UUID> messageIds) {
    return loadMessages().stream()
        .filter(message -> messageIds.contains(message.getId()))
        .toList();
  }

  @Override
  public List<Message> findMessagesByChannelId(UUID channelId) {
    return loadMessages().stream()
        .filter(message -> message.getChannelId().equals(channelId))
        .toList();
  }

  @Override
  public Message findLastMessageInChannel(UUID channelId) {
    List<Message> messagesFromFile = loadMessages();
    Optional<Message> latestMessage = messagesFromFile.stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .max(Comparator.comparing(Message::getCreatedAt));
    return latestMessage.orElse(null);
  }

}