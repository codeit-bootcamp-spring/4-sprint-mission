package com.sprint.mission.discodeit.entity;



public class Message extends BaseEntity {
    private User sender;
    private Channel channel;
    private String content;

    // 생성자
    public Message(User sender, Channel channel, String content) {
        super();
        this.sender = sender;
        this.channel = channel;
        this.content = content;
    }

    // setter && getter
    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }
}
