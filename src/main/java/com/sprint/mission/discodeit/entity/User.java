package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User extends BaseEntity {
    private UUID userId;
    private String userName;
    private UserStatus userStatus;
    private List<Channel> joinedChannels = new ArrayList<>();
    private List<Message> myMessages = new ArrayList<>();

    public User(UUID userId, String userName, UserStatus userStatus) {
        super();
        this.userName = userName;
        this.userId = userId;
        this.userStatus = userStatus;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public List<Channel> getJoinedChannels() {
        return joinedChannels;
    }

    public List<Message> getMyMessages() {
        return myMessages;
        }

    /**
     * 유저를 채널에 추가하고, 채널에도 해당 유저를 동기화하여 추가합니다. (양방향 동기화)*/
    public void addChannel(Channel channel) {
        if (!joinedChannels.contains(channel)) {
            joinedChannels.add(channel);
            channel.addJoinedUser(this);
        }
    }

     /** 유저를 채널에서 제거하고, 채널에서도 해당 유저를 제거합니다. (양방향 동기화)*/
    public void removeChannel(Channel channel) {
        if (joinedChannels.remove(channel)) {
            channel.removeJoinedUser(this);
            }
        }


    /** 유저가 보낸 메세지를 리스트에 등록하고, 메세지의 작성자도 이 유저로 설정합니다. (양방향 동기화)*/
    public void addMessage(Message message) {
        if (!myMessages.contains(message)) {
            myMessages.add(message);
            message.setSender(this);
        }
    }

    /**유저가 보낸 메세지를 목록에서 제거하고, 메세지의 유저 정보를 초기화합니다. (양방향 동기화)*/
    public void removeMessage(Message message) {
        if (myMessages.remove(message)) {
            message.setSender(null);
        }
    }


    @Override
    public String toString() {
        return ("[" +
                "유저 ID: " + userId +
                ", 이름: " + userName +
                ", 상태: " + userStatus +
                ", 생성시각: " + getCreatedAt() +
                ", 참여하는 채널 수:" + joinedChannels.size() +
                ", 내가 보낸 메세지 수: " + myMessages.size() +
                "]");
    }

}


