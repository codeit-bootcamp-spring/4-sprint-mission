package com.sprint.mission.discodeit.entity;
import java.util.UUID;

public class Message extends baseEntity {
    private UUID messageId;
    private String messageBody;
    private User user;
    private Channel channel;

    private static final User deletedUser = new User("deletedUser");
    //작성자가 탈퇴한 경우 메세지의 작성자는 deletedUser로 바뀐다.

    public Message(String messageBody,User user,Channel channel) {
        this.messageId = UUID.randomUUID();
        this.messageBody = messageBody;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
        this.user = user;
        this.channel = channel;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public User getUser() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void removeUser(){
        this.user = deletedUser;
        //작성자가 탈퇴할 경우 메세지는 남아있지만 작성자의 이름은 deletedUser가 된다.
    }

    public void updateMessageBody(String newMessageBody) {
        this.messageBody = newMessageBody;
    }



}
