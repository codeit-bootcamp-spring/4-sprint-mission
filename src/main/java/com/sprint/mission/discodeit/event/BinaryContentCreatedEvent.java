package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BinaryContentCreatedEvent extends ApplicationEvent {
  private final BinaryContent binaryContent;
  private final byte[] data;

  public BinaryContentCreatedEvent(Object source, BinaryContent binaryContent, byte[] data) {
    super(source);
    this.binaryContent = binaryContent;
    this.data = data;
  }


}
