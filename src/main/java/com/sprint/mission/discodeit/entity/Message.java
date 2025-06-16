package com.sprint.mission.discodeit.entity;

import java.io.Serializable;

public class Message extends BaseEntity implements Serializable {

    private String messageContents;
    private User user;
    private Channel channel;

    public Message(User user, Channel channel, String contents) {
        super();
        this.messageContents = contents;
        this.user = user;
        this.channel = channel;
    }

    public String getMessageContents() {
        return messageContents;
    }

    public void updateMessageContent(String messageContents) {
        updateUpdatedAt();
        this.messageContents = messageContents;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void registerMessageToUserAndChannel(User user, Channel channel) {
        user.addMessage(this);
        channel.addMessage(this);
    }

    @Override
    public String toString() {
        return "Message{" +
                "\nmessageId=" + getId() +
                "\nmessageContents='" + messageContents + '\'' +
                "\nuser=" + user.getId() +
                "\nchannel=" + channel.getId() +
                //"\ncreatedAt=" + getCreatedAt() +
                //"\nupdatedAt=" + getUpdatedAt() +
                '}';
    }
}
