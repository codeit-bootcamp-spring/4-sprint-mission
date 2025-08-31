package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends BaseEntity {
    private String channelName;
    private List<User> users;
    private List<Message> messages;

    public Channel(String channelName) {
        this.channelName = channelName;
        this.users = new ArrayList<>();
        this.messages = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateName(String newChannelName) {
        this.channelName = newChannelName;
        touchUpdatedAt();
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.addChannel(this);
        }
    }

    public void deleteUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            user.deleteChannel(this);
        }
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }

    public void deleteMessage(Message message) {
        if (messages.contains(message)) {
            messages.remove(message);
            message.deleteChannel(this);
        }
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                '}';
    }
}
