package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class User extends BaseEntity implements Serializable {

    private final String userId;
    private String username;
    private String email;
    private String phone;
    private Set<Channel> channels;
    private List<Message> messages;
    private static final long serialVersionUID = 1L;

    public User(String username, String email, String phone) {
        super();
        this.userId = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.messages = new ArrayList<>();
        this.channels = new HashSet<>();

    }

    public User(String userId, String username, String email, String phone, long createdAt, long updatedAt) {
        super(createdAt, updatedAt);
        this.userId = userId;
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

    public String toCSV() {
        return String.join(",",
                userId,
                username,
                email,
                phone,
                String.valueOf(createdAt),
                String.valueOf(updatedAt)
        );
    }

    public static User fromCSV(String line) {
        String[] split = line.split(",");
        if (split.length < 6) {
            return null;
        } else {
            String userId = split[0];
            String username = split[1];
            String email = split[2];
            String phone = split[3];
            long createdAt = Long.parseLong(split[4]);
            long updatedAt = Long.parseLong(split[5]);

            return new User(userId,username, email, phone, createdAt, updatedAt);
        }
    }
}