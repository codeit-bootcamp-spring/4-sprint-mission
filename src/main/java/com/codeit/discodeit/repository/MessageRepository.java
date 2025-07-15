package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.Message;
import com.codeit.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessageRepository {

  void createMessage(Message message);

  void deleteMessageByMessageId(UUID messageId);

  void updateMessage(Message message);

  List<Message> loadMessages();

  void saveMessages(List<Message> messages);

  Optional<Message> findMessageByMessageId(UUID id);

  List<Message> findMessagesByMessageIds(Set<UUID> messageIds);

  List<Message> findMessagesByChannelId(UUID channelId);

  void deleteMessagesByChannelId(UUID channelId);

  Message findLastMessageInChannel(UUID channelId);
}