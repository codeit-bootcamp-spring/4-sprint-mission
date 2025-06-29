package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserCreateDto;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID profileId;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private String username;
    private String email;
    private String password;



    public User(String username, String email, String password) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        //
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void update(UserCreateDto userCreateDto) {
        boolean anyValueUpdated = false;
        if (userCreateDto.getUsername() != null && !userCreateDto.getUsername().equals(this.username)) {
            this.username = userCreateDto.getUsername();
            anyValueUpdated = true;
        }
        if (userCreateDto.getEmail() != null && !userCreateDto.getEmail().equals(this.email)) {
            this.email = userCreateDto.getEmail();
            anyValueUpdated = true;
        }
        if (userCreateDto.getPassword() != null && !userCreateDto.getPassword().equals(this.password)) {
            this.password = userCreateDto.getPassword();
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            this.updatedAt = Instant.now();
        }
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }
}