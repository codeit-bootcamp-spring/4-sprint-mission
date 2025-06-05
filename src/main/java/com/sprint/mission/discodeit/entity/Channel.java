package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;

public class Channel extends BaseEntity
{
    private String channelName;
    private User hostUser;

    private ArrayList<Message> messages;
    private ArrayList<User> users;

    public Channel(User hostUser, String name) {
        super();
        this.channelName = name;
        this.hostUser = hostUser;
        this.messages = new ArrayList<>();
        this.users = new ArrayList<>();
    }

    public String getChannelName() {
        return channelName;
    }

    public void updateChannelName(String channelName) {
        super.updateUpdatedAt();
        this.channelName = channelName;
    }

    public User getHostUser() {
        return hostUser;
    }

    public void updateHostUser(User newHostUser) {
        updateUpdatedAt();
        this.hostUser = newHostUser;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
            message.getUser().addMessage(message);
        }
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
            user.removeChannel(this);
        }
    }

    public void clearUsers(){
        users.clear();
    }

    public void clearMessages() {
        messages.clear();
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void removeMessage(Message message) {
        if (messages.contains(message)) {
            messages.remove(message);
            message.getUser().removeMessage(message);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Channel{")
                .append("channelId=").append(getId())
                .append(", channelName='").append(channelName).append('\'')
                .append(", hostUserId=").append(hostUser.getId())
                .append(", createdAt=").append(getCreatedAt())
                .append(", updatedAt=").append(getUpdatedAt())
                .append(", messageIds=").append(messages.stream().map(Message::getId).toList())
                .append(", userIds=").append(users.stream().map(User::getId).toList())
                .append('}');
        return sb.toString();
    }

}
