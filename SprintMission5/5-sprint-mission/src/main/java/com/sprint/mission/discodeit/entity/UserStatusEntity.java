package com.sprint.mission.discodeit.entity;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UserStatusEntity extends BaseEntity {

  private UUID userId;
  @Setter
  private Instant lastActiveAt;

  private static final Duration ONLINE_THRESHOLD = Duration.ofMinutes(5);

  public UserStatusEntity(UUID userId) {
    this.userId = userId;
    this.lastActiveAt = Instant.now();
    setUpdatedAt();
  }

  public void updateAccessTime() {
    this.lastActiveAt = Instant.now();
    setUpdatedAt();
  }

  public void updateAccessTime(Instant time) {
    this.lastActiveAt = time;
    setUpdatedAt();
  }

  public boolean isOnline() {
    Instant threshold = Instant.now().minus(ONLINE_THRESHOLD);
    return lastActiveAt.isAfter(threshold);
  }
}
