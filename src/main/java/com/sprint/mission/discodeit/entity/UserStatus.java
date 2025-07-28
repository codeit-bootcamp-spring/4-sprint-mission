package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Duration;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_statuses")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false)
  private Instant lastActiveAt;

  // 연관관계 편의 메서드
  public void setUser(User user) {
    this.user = user;
  }

  public Boolean isOnline() {
    Instant instantFiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));

    return lastActiveAt.isAfter(instantFiveMinutesAgo);
  }
}
