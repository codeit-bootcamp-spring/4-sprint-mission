package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component("messageSecurity")
public class MessageSecurity {

  private final MessageRepository messageRepository;

  public MessageSecurity(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  public boolean isMessageAuthor(UUID userId, UUID messageId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(userId))
        .orElse(false);
  }

}
