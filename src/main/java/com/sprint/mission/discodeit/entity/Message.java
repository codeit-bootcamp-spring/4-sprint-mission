package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String text;
    private User user;
    private Channel channel;

    public Message(String text, User user, Channel channel) {
        super();
        this.text = text;

        this.user = user;
        this.channel = channel;

        channel.addMessage(this);
        user.addMessage(this);
    }
    public void addChannel(Channel channel) {
        if (channel != null) {
            this.channel = channel;
            channel.addMessage(this);
        }
    }
    public void removeChannel(Channel channel) {
        if (channel != null) {
            this.channel = channel;
            channel.removeMessage(this);
        }
    }
    public void addUser(User user) {
        if (user != null) {
            this.user = user;
            user.addMessage(this);
        }
    }
    public void removeUser(User user) {
        if (user != null) {
            this.user = user;
            user.removeMessage(this);
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return
            "messageId :" + super.getId() + "\n" +
            "messageCreateAt :" + super.getFormattedCreatedAt() + "\n" +
            "messageUpdateAt :" + super.getFormattedUpdatedAt() + "\n" +
            "messageText :" + this.getText() + "\n" +
            "userId :" + (user == null ? "null" : user.getId()) + "\n" +
            "channelId :" + (channel == null ? "null" : channel.getId()) + "\n" +
            "--------------------------------------------------------------";
    }
}
