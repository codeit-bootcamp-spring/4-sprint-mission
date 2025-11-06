//package com.sprint.mission.discodeit.event;
//
//import com.sprint.mission.discodeit.dto.data.MessageDto;
//import com.sprint.mission.discodeit.event.message.MessageCreatedEvent;
//import java.util.UUID;
//import lombok.RequiredArgsConstructor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.event.TransactionPhase;
//import org.springframework.transaction.event.TransactionalEventListener;
//
//@Component
//@RequiredArgsConstructor
//public class WebSocketRequiredEventListener {
//
//  private final SimpMessagingTemplate messagingTemplate;
//
//  // 메세지 커밋 이후
//  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
//  public void handleMessage(MessageCreatedEvent event) {
//    MessageDto messageDto = event.getData();
//    UUID channelId = messageDto.channelId();
//
//    messagingTemplate.convertAndSend("/sub/channels." + channelId + ".messages", messageDto);
//  }
//}
