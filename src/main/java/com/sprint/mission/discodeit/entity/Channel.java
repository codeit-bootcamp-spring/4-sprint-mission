package com.sprint.mission.discodeit.entity;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Channel extends BaseEntity {
    private UUID channelId;
    private String channelName;
    private User host;
    private List<Message> sendedMessages = new ArrayList<>();
    private List<User> joinedUsers = new ArrayList<>();

    public Channel(UUID channelId, String channelName, User host) {
        super();
        this.channelId = channelId;
        this.channelName = channelName;
        this.host = host;
    }

    public UUID getChannelId() {
        return channelId;
    }

    public void setChannelId(UUID channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public List<Message> getSendedMessages() {
        return sendedMessages;
    }

    public List<User> getJoinedUsers() {
        return joinedUsers;
    }

    public void addMessage(Message message) {
        if (!sendedMessages.contains(message)) {
            sendedMessages.add(message);
            message.setChannel(this);
        }
    }

    /**해당 메세지를 해당 채널에서 전송된 메세지 리스트에서 제거하고, 해당 메세지의 채널 참조도 끊습니다. (양방향 동기화)*/
    public void removeMessage(Message message) {
        if (sendedMessages.remove(message)) {
            message.setChannel(null);
        }
    }

    public void addJoinedUser(User user) {
        if (!joinedUsers.contains(user)) {
            joinedUsers.add(user);
            user.addChannel(this);
        }
    }

    /**해당 유저를 채널에 참가한 유저 리스트에서 제거하고, 해당 유저의 채널 목록에서도 제거함 (양방향 동기화)*/
    public void removeJoinedUser(User user) {
        if (joinedUsers.remove(user)) {
            user.removeChannel(this);
        }
    }

    @Override
    public String toString() {
        return "[" +
                "채널 ID: " + channelId +
                ", 채널명: " + channelName +
                ", 호스트: " + host +
                ", 생성 시각: " + getCreatedAt() +
                ", 수정 시각: " + getUpdatedAt() +
                "]";
    }

}
