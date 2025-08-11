package com.codeit.discodeit.dto.user_status_dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserStatusUpdateRequest {

  private Instant newLastActiveAt;

  // getter, setter
  public UserStatusUpdateRequest() {
  }
}
