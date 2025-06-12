package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;

public class Channel extends BaseEntity implements Serializable {

    private final String channelId;
    private String channelName;
    private String description;
    private Set<User> users;
    private Set<Message> messages;
    private static final long serialVersionUID = 1L;


    public Channel(String channelName, String description) {

        super();
        this.channelId = UUID.randomUUID().toString();
        this.channelName = channelName;
        this.description = description;
        this.users = new HashSet<>();
        this.messages = new HashSet<>();
    }

    public Channel(String channelId, String channelName, String description, long  createdAt, long  updatedAt) {
        super(createdAt, updatedAt);
        this.channelId = channelId;
        this.channelName = channelName;
        this.description = description;
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

    public void addMessage(Message message) {
        messages.add(message);
    }

    public void removeMessage(Message message) {
        messages.remove(message);
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

    public String toCSV() {
        return String.join(",",
                channelId,
                channelName,
                description,
                String.valueOf(createdAt),
                String.valueOf(updatedAt)
        );
    }

    public static Channel fromCSV(String line) {
        String[] split = line.split(",");
        if (split.length < 6) {
            return null;
        } else {
            String channelId = split[0];
            String channelName = split[1];
            String description = split[2];
            long createdAt = Long.parseLong(split[4]);
            long updatedAt = Long.parseLong(split[5]);

            return new Channel(channelId,channelName, description, createdAt, updatedAt);
        }
    }
}