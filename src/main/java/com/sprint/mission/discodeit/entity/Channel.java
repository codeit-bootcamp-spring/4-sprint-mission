package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel extends BaseEntity implements Serializable {
    private String channelName;
    private User hostUser;

    private final ArrayList<Message> messages = new ArrayList<>();
    private final ArrayList<User> users  = new ArrayList<>();

    public Channel(User hostUser, String name) {
        super();
        this.channelName = name;
        this.hostUser = hostUser;
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
        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                // 같은 걸 찾믕
                //System.out.println("동일 유저가 안에 있네요");
                return;
            }
        }
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
                .append("\nchannelName='").append(channelName).append('\'')
                .append("\nhostUserId=").append(hostUser.getId())
                //.append("\ncreatedAt=").append(getCreatedAt())
                //.append("\nupdatedAt=").append(getUpdatedAt())
                .append("\nmessageIds=").append(messages.stream().map(Message::getId).toList())
                .append("\nuserIds=").append(users.stream().map(User::getId).toList())
                .append('}');
        return sb.toString();
    }
}
