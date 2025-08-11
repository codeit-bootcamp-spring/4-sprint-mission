package com.codeit.discodeit.entity;

import com.codeit.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userStatuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatus extends BaseUpdatableEntity {

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "lastActiveAt", nullable = false)
  private Instant lastActiveAt = Instant.EPOCH;

  public void setUser(User user) {
    this.user = user;
    if (user.getStatus() != this) {
      user.setStatus(this); // 양방향 모두 연결
    }
  }
}