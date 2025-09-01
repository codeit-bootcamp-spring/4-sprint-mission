package com.codeit.discodeit8.dto.readstatus_dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReadStatusUpdateRequest {

  private Instant newLastReadAt;
}
