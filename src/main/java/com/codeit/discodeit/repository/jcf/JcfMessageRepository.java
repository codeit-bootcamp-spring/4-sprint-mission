package com.codeit.discodeit.repository.jcf;

import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class JcfMessageRepository implements MessageRepository {

  private List<Message> messageData = new ArrayList<>();

  @Override
  public List<Message> loadMessages() {
    return messageData;
  }

  @Override
  public void saveMessages(List<Message> messages) {
    messageData = messages;
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