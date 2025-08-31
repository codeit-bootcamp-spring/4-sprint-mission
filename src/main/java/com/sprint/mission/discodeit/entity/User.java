package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String email;
    private String password;
    private String username;
    private String displayName;
    private UserStatus status;

    private List<Channel> channels;
    private List<Message> messages;

    public User() {
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public User(String email, String password, String username, String displayName) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.displayName = displayName;
        this.status = UserStatus.ACTIVE; // 기본 활성화 상태
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
    }


    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPassword() {
        return password;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void updateUserDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;
        touchUpdatedAt();
    }

    public void updateUserStatus(UserStatus newStatus) {
        this.status = newStatus;
        touchUpdatedAt();
    }


    public void addChannel(Channel channel) {
        if (!channels.contains(channel)) {
            channels.add(channel);
            channel.addUser(this);
        }
    }

    public void deleteChannel(Channel channel) {
        if (channels.contains(channel)) {
            channels.remove(channel);
            channel.deleteUser(this);
        }
    }


    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", status=" + status +
                '}';
    }
}
