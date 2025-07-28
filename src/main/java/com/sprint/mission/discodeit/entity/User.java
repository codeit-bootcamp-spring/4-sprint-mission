package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
public class User extends BaseUpdatableEntity {

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id", nullable = true) // fk는 user 테이블에 존재
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus status;

  // 연관관계 편의 메서드
  public void setProfile(BinaryContent profile) {
    this.profile = profile;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
    if (status.getUser() != this) {
      status.setUser(this);
    }
  }
}
