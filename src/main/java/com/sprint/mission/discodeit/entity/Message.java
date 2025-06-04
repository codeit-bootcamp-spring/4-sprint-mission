package com.sprint.mission.discodeit.entity;

import java.util.List;

public class Message extends BaseEntity {
    private User user;
    private Channel channel;
    private String content;

    List<Channel> channels;


    public Message(User user, Channel channel, String content) {
        this.user = user;
        this.channel = channel;
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getContent() {
        return content;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        if(!channels.contains(channel)) {
            channels.add(channel);
            channel.addMessage(this);
        }
    }

    public void deleteChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.deleteMessage(this);
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                '}';
    }
}
