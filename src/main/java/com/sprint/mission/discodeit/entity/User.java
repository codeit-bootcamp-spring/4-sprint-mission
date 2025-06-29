package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.DTO.UserCreateRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    private String username;
    private String email;
    private String password;
    private UUID profileId;

    public User(UserCreateRequest userCreateRequest) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.username = userCreateRequest.username();
        this.email = userCreateRequest.email();
        this.password = userCreateRequest.password();
    }

//    public void update(String newUsername, String newEmail, String newPassword) {
//        boolean anyValueUpdated = false;
//        if (newUsername != null && !newUsername.equals(this.username)) {
//            this.username = newUsername;
//            anyValueUpdated = true;
//        }
//        if (newEmail != null && !newEmail.equals(this.email)) {
//            this.email = newEmail;
//            anyValueUpdated = true;
//        }
//        if (newPassword != null && !newPassword.equals(this.password)) {
//            this.password = newPassword;
//            anyValueUpdated = true;
//        }
//
//        if (anyValueUpdated) {
//            this.updatedAt = Instant.now();
//        }
//    } 
    //비즈니스 로직이라고 생각되어 서비스로 이관

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
        this.updatedAt = Instant.now();
    }
}
