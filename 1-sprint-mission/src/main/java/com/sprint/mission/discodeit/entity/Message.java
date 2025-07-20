package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.*;


public class Message extends BaseEntity implements Serializable {

    private final String messageId;
    private String content;
    private  User user;
    private  Channel channel;
    private static final long serialVersionUID = 1L;

    public Message(User user, Channel channel, String content) {
        super();
        this.messageId = UUID.randomUUID().toString();
        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}