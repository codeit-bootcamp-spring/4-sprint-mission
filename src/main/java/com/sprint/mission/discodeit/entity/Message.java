package com.sprint.mission.discodeit.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message extends BaseEntity {
    private UUID messageId;
    private String messageContent;
    private User sender;
    private Channel channel;

    public Message(UUID messageId, User sender, Channel channel, String messageContent) {
        super();
        this.messageId = messageId;
        this.messageContent = messageContent;
        this.sender = sender;
        this.channel = channel;

        if(sender != null) {
            sender.addMessage(this);
        }

        if(channel != null) {
            channel.addMessage(this);
        }
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public User getSender() {
        return sender;
    }

    /**메세지의 작성자를 설정합니다.
     * 단방향 설정만 수행하며 양방향 동기화를 위해서는 외부에서 추가 조치가 필요합니다.*/
    public void setSender(User sender) {
        this.sender = sender;
    }

    public Channel getChannel() {
        return channel;
    }

    /**메세지가 전송된 채널을 설정합니다.
     * 단방향 설정만 수행하며 양방향 동기화를 위해서는 외부에서 추가 조치가 필요합니다.*/
    public void setChannel(Channel channel) {
        this.channel = channel;
    }


    @Override
    public String toString() {
        return "[" +
                "메세지 ID: " + messageId +
                ", 작성자: " + sender.getUserName() +
                ", 채널: " + channel.getChannelName() +
                ", 메세지 내용: \"" + messageContent + "\"" +
                ", 생성 시각: " + getCreatedAt() +
                ", 수정 시각: " + getUpdatedAt() +
                "]";
    }

}
