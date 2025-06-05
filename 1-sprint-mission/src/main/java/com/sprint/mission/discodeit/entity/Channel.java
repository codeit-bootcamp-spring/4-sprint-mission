package com.sprint.mission.discodeit.entity;

import java.util.*;

public class Channel extends BaseEntity {

    private final String channelId;
    private String channelName;
    private String description;
    private Set<User> users;
    private Set<Message> messages;

    public Channel(String channelName, String description) {

        super();
        this.channelId = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.description = description;
        this.users = new HashSet<>();
    }

    public String getChannelId() {
        return channelId;
    }

    public String getChannelname() {
        return channelName;
    }

    public void setChannelname(String channelName) {
        this.channelName = channelName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelId='" + channelId + '\'' +
                ", name='" + channelName + '\'' +
                ", desc='" + description + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

