package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("messageAuthChecker")
public class MessageAuthChecker {

  private final MessageRepository messageRepository;

  public MessageAuthChecker(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository
        .findById(messageId)
        .map(m -> m.getAuthor().getId().equals(userId))
        .orElse(false); // 메시지가 없으면 false
  }
}
