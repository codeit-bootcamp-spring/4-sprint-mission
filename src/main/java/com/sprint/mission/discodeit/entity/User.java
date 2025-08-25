package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;

public class User extends BaseEntity {

    private String userName;

    private ArrayList<Channel> channels;
    private ArrayList<Message> messages;

    private UserStatus status;
    // 유저가 회원가입 상태인지 탈퇴 상태인지를 판별
    // 유저 조회 할 때 조회 되지는 않지만 데이터를 완전 삭제하지 않은 상태이다.

    // enum 추가
    // 팩토리 패턴을 이용해보기
    // 상태추가
    // forEach에서 데이터 값 바꾸지 않기, map에서 바꾸기

    public enum UserStatus {
        ACTIVE,
        DEACTIVE // true와 false 로나누는 것보단 확장성이 더 좋을 것 같다.
    }

    public User(String userName) {
        super();
        this.userName = userName;
        this.channels = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.status = UserStatus.ACTIVE;
    }

    public String getUserName() {
        return userName;
    }

    public ArrayList<Channel> getChannels() {
        return channels;
    }

    public void addChannel(Channel channel) {
        if(!channels.contains(channel)) {
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
                ", userName='" + userName + '\'' +
                //", createdAt=" + getCreatedAt() +
                //", updatedAt=" + getUpdatedAt() +
                ", channels=" + channels.stream().map(channel -> channel.getId()).toList() +
                '}';
    }
}

// remove 통일