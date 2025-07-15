package com.codeit.discodeit.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
@Schema(description = "User 상태 정보")
public class UserStatus implements Serializable {

  private UUID id;

  private Instant createdAt;

  private Instant updatedAt;

  private UUID userId;

  private Instant lastActiveAt;

  private boolean online;

  public UserStatus(User user) {
    this.id = UUID.randomUUID();
    this.createdAt = Instant.now();
    this.updatedAt = createdAt;
    this.userId = user.getId();
    this.lastActiveAt = createdAt;
    this.online = true;
  }

  public void updateUserStatus() {
    this.updatedAt = Instant.now();
    this.lastActiveAt = updatedAt;
  }

  public boolean getOnline() {
    if (lastActiveAt == null) {
      this.online = false;
    } else {
      Duration duration = Duration.between(lastActiveAt, Instant.now());
      this.online = Math.abs(duration.toMinutes()) <= 5;
    }
    return this.online;
  }
}