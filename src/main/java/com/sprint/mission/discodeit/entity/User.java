package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private String username;
    private String email;
    private String password;

    private UUID profileId;

    public User(String username, String email, String password, UUID profileId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void updateUsername(String newUsername) {
        this.username = newUsername;
    }

    public void updateEmail(String newEmail) {
        this.email = newEmail;
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateProfileId(UUID newProfileId) {
        this.profileId = newProfileId;
    }

    public void touch() {
        this.updatedAt = Instant.now();
    }
}
