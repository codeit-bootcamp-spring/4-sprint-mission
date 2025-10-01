package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("MessageAuth")
@RequiredArgsConstructor
public class MessageAuth {
  private final MessageRepository messageRepository;

  public boolean isMessageOwner(UUID messageId, UUID currentUserId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(currentUserId))
        .orElse(false);
  }

}
