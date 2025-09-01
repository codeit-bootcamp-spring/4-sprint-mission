package com.codeit.discodeit8.entity;

import com.codeit.discodeit8.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseUpdatableEntity {

  @Column(name = "username", nullable = false, unique = true, length = 50)
  private String username;

  @Column(name = "email", nullable = false, unique = true, length = 100)
  private String email;

  @Column(name = "password", nullable = false, length = 60)
  private String password;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "profile", nullable = true)
  @OnDelete(action = OnDeleteAction.SET_NULL)
  private BinaryContent profile;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "user_id")
  private UserStatus status;

  public void setStatus(UserStatus status) {
    this.status = status;
    if (status.getUser() != this) {
      status.setUser(this); // 양방향 모두 연결
    }
  }
}