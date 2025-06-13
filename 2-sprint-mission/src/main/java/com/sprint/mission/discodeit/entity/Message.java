package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Message extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID messageId;
    private User user;
    private Channel channel;
    private String contents;
    private MessageState state;


    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String created = sdf.format(new Date(getCreatedAt()));
        String updated = sdf.format(new Date(getUpdatedAt()));

        return "Message{" +"\n"+
                "messageContents=" + contents +"\n"+
                "userName=" + user.getUserName() +"\n"+
                "channelName=" + channel.getChannelName() +"\n"+
                "createdAt=" + created +"\n"+
                "updatedAt=" + updated +"\n"+
                '}'+"\n";
    }

    public Message(String messageContents, User user, Channel channel) {
        messageId = UUID.randomUUID();
        this.contents = messageContents;
        this.user = user;
        this.channel = channel;
        user.getMessages().add(this);
        channel.getChannelMessages().add(this);
    }

    //양방향 연결
    public void addChannel(Channel channel) {
        if(this.channel == null) {
            this.channel = channel;
            channel.addMessage(this);
        }
    }

    public void removeChannel(Channel channel) {
        if(this.channel == channel) {
            this.channel = null;
            channel.removeMessage(this);
        }
    }

    public void addUser(User user) {
        if(this.user == null) {
            this.user = user;
            user.addMessage(this);
        }
    }
    public void removeUser(User user) {
        if(this.user != user) {
            this.user = null;
            user.removeMessage(this);
        }
    }

    //상태
    public void visibleMessageState() {
        state = MessageState.VISIBLE;
    }
    public void invisibleMessageState() {
        state = MessageState.INVISIBLE;
    }
    public void deletedMessageState() {
        state = MessageState.DELETED;
    }


    public MessageState getState() {
        return state;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getContent() {
        return contents;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

}
