package com.codeit.discodeit.dto.readstatus_dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadStatusUpdateRequest {

  private Instant newLastReadAt;

  public ReadStatusUpdateRequest(Instant newLastReadAt) {
    this.newLastReadAt = newLastReadAt;
  }

  public ReadStatusUpdateRequest() {
  }
}
