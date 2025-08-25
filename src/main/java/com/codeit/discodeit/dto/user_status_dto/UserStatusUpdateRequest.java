package com.codeit.discodeit.dto.user_status_dto;

import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserStatusUpdateRequest {

  private Instant newLastActiveAt;
}
