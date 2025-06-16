package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class User extends BaseEntity implements Serializable {

    private String userName;

    private ArrayList<Channel> channels = new ArrayList<>();;
    private ArrayList<Message> messages = new ArrayList<>();;

    private UserStatus status;
    // 유저가 회원가입 상태인지 탈퇴 상태인지를 판별
    // 유저 조회 할 때 조회 되지는 않지만 데이터를 완전 삭제하지 않은 상태이다.

    public User(String userName) {
        super();
        this.userName = userName;
        this.status = UserStatus.ACTIVE;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        for (Channel c : channels) {
            if(c.getId().equals(channel.getId())) {
                // 같은 걸 찾믕
                System.out.println("동일 채널이 안에 있네요");
                return;
            }
        }

        if(!channels.contains(channel)) {
            System.out.println("1231231"+channel.getUsers());
            channels.add(channel);
            channel.addUser(this);
        }
    }

    public void removeChannel(Channel channel) {
        if(channels.contains(channel)) {
            channels.remove(channel);
            channel.removeUser(this);
        }
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message message) {
        if(!this.messages.contains(message)) {
            this.messages.add(message);
        }
    }

    public void removeMessage(Message message) {
        if(messages.contains(message)) {
            System.out.println("메세지 삭제 성공적 유저 에서");
            this.messages.remove(message);
            message.getChannel().getMessages().remove(message);
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
        updateUpdatedAt();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status; // 유저 삭제와 유저 복구에 쓰일 수 있다.
    }

    public void clearChannels() {
        channels.clear();
    }

    public void clearMessages() {
        messages.clear();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + getId() +
                "\nuserName='" + userName  +
                //", createdAt=" + getCreatedAt() +
                //", updatedAt=" + getUpdatedAt() +
                //"\nchannels=" + channels.stream().map(Channel::toString).toList() +
                "\nmessages=" + messages.stream().map(Message::toString).toList() +
                '}';
    }
}