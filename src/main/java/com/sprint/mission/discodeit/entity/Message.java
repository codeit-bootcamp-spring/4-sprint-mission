package com.sprint.mission.discodeit.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String messageContents;
    private final User user;
    private final Channel channel;

    public Message(String contents, User user, Channel channel) {
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

    // toString
    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + super.getId() +
                ", messageContents='" + messageContents + '\'' +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", user=" + "\"" + user.getUserName() + "\" [" + user.getId() + "]" +
                ", channel=" + "\"" + channel.getChannelName() + "\" [" + channel.getId() + "]" +
                "}\n";
    }
}
