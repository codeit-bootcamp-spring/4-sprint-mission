package com.sprint.mission.discodeit.event.listener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

import com.sprint.mission.discodeit.event.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WebSocketRequiredEventListener {
  private final SimpMessagingTemplate messagingTemplate;
  private final MessageMapper messageMapper;

  @TransactionalEventListener(phase = AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event) {
    messagingTemplate.convertAndSend("/sub/channels." + event.message().getChannel().getId() + ".messages", messageMapper.toDto(event.message()));
  }


}
