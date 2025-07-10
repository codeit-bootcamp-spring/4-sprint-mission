package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델입니다. 사용자의 온라인 상태를 확인하기 위해 활용합니다.
 * <p>
 * 마지막 접속 시간을 기준으로 현재 로그인한 유저로 판단할 수 있는 메소드를 정의하세요. 마지막 접속 시간이 현재 시간으로부터 5분 이내이면 현재 접속 중인 유저로 간주합니다.
 * Ex) User Id: 123, lastActiveAt:(time), active: false
 */
@Getter
public class UserStatus extends BaseEntity {

  private final UUID userId;
  private Instant lastActiveTime;
  @Setter
  private UserState status;

  public UserStatus(UUID userId) {
    super();
    this.userId = userId;
    this.lastActiveTime = Instant.now();
    this.status = UserState.ONLINE;
  }

  public enum UserState {
    ONLINE, OFFLINE
  }

  public void updateLastActiveTime() {
    this.lastActiveTime = Instant.now();
    this.updateTimeStamp();
  }

  public void update(Instant lastActiveAt) {
    boolean anyValueUpdated = false;
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveTime)) {
      this.lastActiveTime = lastActiveAt;
      anyValueUpdated = true;
    }

    if (anyValueUpdated) {
      this.updatedAt = Instant.now();
    }
  }

}
