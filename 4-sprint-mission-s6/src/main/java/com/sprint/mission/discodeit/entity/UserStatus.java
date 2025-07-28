package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_status")
public class UserStatus extends BaseUpdatableEntity {


  //
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "last_active_at", nullable = false)
  private Instant lastActiveAt;

  public UserStatus(User user, Instant lastActiveAt) {
    //
    this.lastActiveAt = lastActiveAt;
    setUser(user);
  }

  /* 외부 공개 메서드 */
  public void setUser(User newUser) {
    if (this.user == newUser) return;

    // 기존 관계 끊기
    if (this.user != null) {
      this.user.setUserStatus(null);
    }

    this.user = newUser;

    // 새 관계 연결
    if (newUser != null && newUser.getUserStatus() != this) {
      newUser.setUserStatus(this);
    }
  }

  /* User에서만 호출하도록 내부 전용 메서드 (package-private 또는 private) */
  void internalSetUser(User user) {
    this.user = user;
  }

  public void update(Instant lastActiveAt) {
    if (lastActiveAt != null && !lastActiveAt.equals(this.lastActiveAt)) {
      this.lastActiveAt = lastActiveAt;
    }
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
