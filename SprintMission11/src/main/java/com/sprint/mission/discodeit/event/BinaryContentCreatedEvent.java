package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BinaryContentCreatedEvent extends ApplicationEvent {

  private final UUID binaryContentId;
  private final byte[] bytes;

  public BinaryContentCreatedEvent(Object source, UUID binaryContentId, byte[] bytes) {
    super(source);
    this.binaryContentId = binaryContentId;
    this.bytes = bytes;
  }
}
