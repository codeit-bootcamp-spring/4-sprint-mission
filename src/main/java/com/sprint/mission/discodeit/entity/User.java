package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User extends BasedEntity {
    private UUID profileId;
    private String username;
    private String email;
    private String password;
    private PresenceStatus presenceStatus;

    public User(String username, String password, String email, UUID profileId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profileId = profileId;
        this.presenceStatus = PresenceStatus.ONLINE;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + getId() +
                ", userCreatedAt=" + getCreatedAt() +
                ", userUpdatedAt=" + getUpdatedAt() +
                ", userName='" + username + '\'' +
                ", email= " + email +
                ", password= " + password +
                ", status=" + presenceStatus.getValue() +
                '}';
    }
}
