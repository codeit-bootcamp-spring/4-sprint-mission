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

    public Message(String messageId, User user, Channel channel, String content, long createdAt, long updatedAt) {
        super(createdAt, updatedAt);
        this.messageId = messageId;
        this.user = user;
        this.channel = channel;
        this.content = content;
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

    public String toCSV() {
        return String.join(",",
                messageId,
                user.getUserId(),
                channel.getChannelId(),
                content,
                String.valueOf(createdAt),
                String.valueOf(updatedAt)
        );
    }

    public static Message fromCSV(String line, User user, Channel channel) {
        String[] split = line.split(",");

        if (split.length < 6) {
            return null;
        } else {
            String messageId = split[0];
//            String userId = split[1];
//            String channelId = split[2];
            String contnent = split[3];
            long createdAt = Long.parseLong(split[4]);
            long updatedAt = Long.parseLong(split[5]);

            return new Message(messageId,user, channel, contnent, createdAt, updatedAt);
        }
    }
}