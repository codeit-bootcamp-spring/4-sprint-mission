package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;
import java.time.Clock;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MessageCreatedEvent extends ApplicationEvent {

  public final Message message;

  public MessageCreatedEvent(Object source, Message message) {
    super(source);
    this.message = message;
  }

  public MessageCreatedEvent(Object source, Clock clock, Message message) {
    super(source, clock);
    this.message = message;
  }
}
