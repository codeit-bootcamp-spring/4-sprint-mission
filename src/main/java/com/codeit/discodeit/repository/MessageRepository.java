package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface MessageRepository {

  void createMessage(Message message);

  void deleteMessage(Message message);

  void updateMessage(Message message);

  Optional<Message> findMessageByMessageId(UUID id);

  List<Message> findMessagesByChannelId(UUID channelId);

  Optional<Message> findLastMessageInChannel(UUID channelId);
}