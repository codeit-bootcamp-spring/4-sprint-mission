package com.sprint.mission.discodeit.event;

import org.springframework.context.ApplicationEvent;

public class S3UploadFailedEvent extends ApplicationEvent {

  public S3UploadFailedEvent(Object source) {
    super(source);
  }
}
