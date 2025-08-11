package com.codeit.discodeit.dto.readstatus_dto;

import java.time.Instant;
import java.util.UUID;

  public record ReadStatusDto(
      UUID id,
      UUID userId,
      UUID channelId,
      Instant lastReadAt
  ) {
  }