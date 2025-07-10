package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;
import java.io.Serial;
import java.util.UUID;

@Getter
public class User extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  @Setter
  private String username;
  @Setter
  private String email;
  @Setter
  private String password;
  @Setter
  private UUID profileId;

  public User(String username, String email, String password, UUID profileId) {
    super();
    this.username = username;
    this.email = email;
    this.password = password;
    this.profileId = profileId;
  }

}
