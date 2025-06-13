package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class Message extends BaseEntity {

    private String messageContents;

    private final User user;
    private final Channel channel;

    public Message(User user, Channel channel, String contents) {
        super();
        this.messageContents = contents;
        this.user = user;
        this.channel = channel;
    }

    // Getter/Setter
    public String getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(String messageContents) {
        this.messageContents = messageContents;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + id +
                ", messageContents='" + messageContents + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", user=" + user +
                ", channel=" + channel +
                '}';
    }
}