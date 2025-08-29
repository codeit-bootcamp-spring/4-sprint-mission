package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseUpdatableEntity {
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    @OneToOne(orphanRemoval = true, cascade = {CascadeType.ALL}, mappedBy = "user")
    private UserStatus status;

    public User(String username, String password, String email, BinaryContent profile) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
    }

    public User(UserCreateRequest request, BinaryContent profile) {
        super();
        this.username = request.username();
        this.email = request.email();
        this.password = request.password();
        this.profile = profile;
    }

    private User(UUID uuid, String username, String email, String password, BinaryContent profile) {
        setId(uuid);
        this.username = username;
        this.email = email;
        this.password = password;
        this.profile = profile;
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
                '}';
    }

    public UUID getProfileId() {
        return profile.getId();
    }

    public static User of(UUID uuid, String username, String email, String password, BinaryContent profile) {
        return new User(uuid, username, email, password, profile);
    }
}

