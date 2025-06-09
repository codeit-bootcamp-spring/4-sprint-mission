package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {

    private final String userId;
    private String username;
    private String email;
    private String phone;
    private Set<Channel> channels;
    private List<Message> messages;

    public User(String username, String email, String phone) {
        super();
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.phone = phone;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Channel> getChannels() {
        return new ArrayList<>(channels);
    }

    public void setChannels(Set<Channel> channels) {
        this.channels = channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + userId +
                ", name='" + username + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
