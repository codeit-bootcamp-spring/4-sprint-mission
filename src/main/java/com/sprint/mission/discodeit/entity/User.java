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

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseUpdateEntity implements Serializable{

  private static final long serialVersionUID = 1L;

  @Column(unique = true, nullable = false, length = 50)
  private String username;

  @Column(unique = true, nullable = false, length = 60)
  private String email;

  @Column(nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, optional = true)
  @JoinColumn(name = "profile", nullable = true)
  private BinaryContent profile;
  public void setProfile(BinaryContent profile) {
    this.profile = profile;
  }

//  @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
//  private List<ReadStatus> readStatusList = new ArrayList<>();

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private UserStatus userStatus;

  public void setUserStatus(UserStatus userStatus) {
    this.userStatus = userStatus;
    if (userStatus != null && userStatus.getUser() != this) {
      userStatus.setUser(this);
    }
  }

//  public void setReadStatus(ReadStatus readStatus) {
//    readStatusList.add(readStatus);
//    if(readStatus!=null&&readStatus.getUser()!=this) {
//      readStatus.setUser(this);
//    }
//  }

  public User(String username, String email, String password, BinaryContent profile) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
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
}
