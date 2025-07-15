package com.codeit.discodeit.entity;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class ReadStatus extends BaseEntity {

  private final UUID channelId;
  private final UUID userId;
  private Instant lastReadAt;

  public ReadStatus(UUID userId, UUID channelId) {
    this.userId = userId;
    this.channelId = channelId;
    this.lastReadAt = Instant.now();
  }

  public void updateLastReadAt() {
    this.lastReadAt = Instant.now();
  }
}