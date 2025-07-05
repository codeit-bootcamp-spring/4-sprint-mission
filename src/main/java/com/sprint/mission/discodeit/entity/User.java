package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.baseentity.BaseEntity;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    //
    private String username;
    private String email;
    private String password;
    private UUID profileId;     // BinaryContent

    public User(String username, String email, String password, UUID profileId) {
        super();
        //
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
    }

    public void update(String newUsername, String newEmail, String newPassword, UUID newProfileId) {
        System.out.println("# User: update");
        boolean anyValueUpdated = false;
        if (newUsername != null && !newUsername.equals(this.username)) {
            this.username = newUsername;
            anyValueUpdated = true;
        }
        if (newEmail != null && !newEmail.equals(this.email)) {
            this.email = newEmail;
            anyValueUpdated = true;
        }
        if (newPassword != null && !newPassword.equals(this.password)) {
            this.password = newPassword;
            anyValueUpdated = true;
        }
        if (newProfileId != null && !newProfileId.equals(this.profileId)) {
            this.profileId = newProfileId;
            anyValueUpdated = true;
        }

        if (anyValueUpdated) {
            super.setUpdatedAt(Instant.now());
        }
    }

    public UserDto.response toDto(Boolean online) {
        return new UserDto.response(
                super.getId(),
                super.getCreatedAt(),
                super.getUpdatedAt(),
                username,
                email,
                profileId,
                online
        );
    }
}
