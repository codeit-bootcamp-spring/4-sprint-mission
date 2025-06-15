package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private List<Channel> channels;
    private List<Message> messages;

    public User(String userName) {
        super();
        this.userName = userName;

        channels = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public void addChannel(Channel channel) {
        if(!channels.contains(channel)) {
            channels.add(channel);
        }
    }
    public void removeChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeUser(this);
        }
    }
    public void addMessage(Message message) {
        if(!messages.contains(message)) {
            messages.add(message);
        }
    }
    public void removeMessage(Message message) {
        if(messages.contains(message)) {
            messages.remove(message);
            message.removeUser(this);
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
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
        return "userId :" + super.getId() + "\n" +
                "userCreateAt :" + super.getFormattedCreatedAt() + "\n" +
                "userUpdateAt :" + super.getFormattedUpdatedAt() + "\n" +
                "userName :" + this.getUserName() + "\n" +
                "channelsId :" + (channels == null ? "null" : channels.stream()
                .map(c -> c.getId().toString())
                .collect(Collectors.joining(", "))) + "\n" +
                "messagesId :" + (messages == null ? "null" : messages.stream()
                .map(m -> m.getId().toString())
                .collect(Collectors.joining(", "))) + "\n" +
                "--------------------------------------------------------------";
    }
}
