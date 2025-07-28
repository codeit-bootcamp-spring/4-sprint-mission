package com.sprint.mission.discodeit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  //
  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;
  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile_id")
  private BinaryContent profile;     // BinaryContent

  /*유저 상태*/
  @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus userStatus;

  public User(String username, String email, String password, BinaryContent profile) {
    //
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
  }

  /* 양방향 편의 메서드 */
  public void setUserStatus(UserStatus newStatus) {
    if (this.userStatus == newStatus) return;

    // 기존 관계 끊기
    if (this.userStatus != null) {
      this.userStatus.internalSetUser(null);
    }

    this.userStatus = newStatus;

    // 새 관계 연결
    if (newStatus != null && newStatus.getUser() != this) {
      newStatus.internalSetUser(this);
    }
  }

  public void update(String newUsername, String newEmail, String newPassword, BinaryContent newProfile) {
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null && !newProfile.equals(this.profile)) {
      this.profile = newProfile;
    }
  }

  //프로필 이미지 교체
  public void updateProfile(BinaryContent newProfile) {
    this.profile = newProfile;
  }
}
