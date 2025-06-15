package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Channel extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    private String channelName;
    private List<User> users;
    private List<Message> messages;

    public Channel(String channelName, User user) {
        super();
        this.channelName = channelName;

        users = new ArrayList<>();
        messages = new ArrayList<>();

        users.add(user);
        user.addChannel(this);
    }

    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.addChannel(this);
        }
    }
    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
        }
    }
    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
        }
    }
    public void removeMessage(Message message) {
        if (messages.contains(message)) {
            messages.remove(message);
        }
    }
    @Override
    public String toString() {
        return
           "channelId :" + super.getId() + "\n" +
           "channelCreateAt :" + super.getFormattedCreatedAt() + "\n" +
           "channelUpdateAt :" + super.getFormattedUpdatedAt() + "\n" +
            "channelName :" + this.getChannelName() + "\n" +
            "usersId :" + (users == null ? "null" : users.stream()
                   .map(u -> u.getId().toString())
                   .collect(Collectors.joining(", "))) + "\n" +
            "messagesId :" + (messages == null ? "null" : messages.stream()
                   .map(m -> m.getId().toString())
                   .collect(Collectors.joining(", "))) + "\n" +
            "--------------------------------------------------------------";
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
