package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageCreatedEventListener {

  private final ReadStatusRepository readStatusRepository;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handleMessageCreatedEvent(MessageCreatedEvent event){
    Message message = event.getMessage();

    List<ReadStatus> subscribers = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(message.getChannel().getId());

    for (ReadStatus subscriber : subscribers) {
      log.info("[{}] 채널에 새 메세지 알림 : 사용자 = {}, 메세지ID = {}", message.getChannel().getId(), subscriber.getUser().getId(), message.getId());
    }


  }
}
