package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class User extends BaseEntity {
    private String username;
    private List<Channel> channels = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Message> getMessages() {
        return messages;
    }

    // 생성자
    public User(String name) {
        super();
        this.username = name;
    }

    // getter && setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
