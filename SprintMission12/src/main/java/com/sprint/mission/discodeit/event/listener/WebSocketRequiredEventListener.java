package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class WebSocketRequiredEventListener {
  private final SimpMessagingTemplate simpMessagingTemplate;

  public WebSocketRequiredEventListener(SimpMessagingTemplate simpMessagingTemplate) {
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessage(MessageCreatedEvent event){
    MessageDto messageDto = event.getData();

    String destination = "/sub/channels." + messageDto.channelId() + ".messages";

    simpMessagingTemplate.convertAndSend(destination, messageDto);
  }
}
