package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
public class User extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @Setter private UUID profileId;
    private List<UUID> channelIds = new ArrayList<>();
    private List<UUID> messageIds = new ArrayList<>();
    @Setter private String userName;
    private AccountState state;
    private String userEmail;
    private String userPassword;

    public User(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        state = AccountState.ACTIVE;
        profileId = null;
    }

    @Override
    public String toString() {
        String channelIdString =
                channelIds.stream().map(UUID::toString).collect(Collectors.joining(", "));

        return "User{"
                + "\n"
                + "userName="
                + userName
                + "\n"
                + "userState="
                + state
                + "\n"
                + "channelIds="
                + channelIdString
                + "\n"
                + "createdAt="
                + getCreatedAt()
                + "\n"
                + "updatedAt="
                + getUpdatedAt()
                + "\n"
                + '}'
                + "\n";
    }

    // 양방향 연결
    public void addChannel(Channel channel) {
        if (channelIds.contains(channel.getId())) {
            throw new IllegalArgumentException("이미 유저가 참가해있는 생태입니다.");
        }
        channelIds.add(channel.getId());
        channel.getUserIds().add(this.getId());
    }

    public void removeChannel(Channel channel) {
        if (!channelIds.contains(channel.getId())) {
            throw new IllegalArgumentException("해당 채널에 유저가 참가해있지 않습니다.");
        }
        channelIds.remove(channel.getId());
        channel.getUserIds().remove(this.getId());
    }

    public void addMessage(Message message) {
        if (messageIds.contains(message.getId())) {
            throw new IllegalArgumentException("해당 메세지는 유저가 쓴 메세지에 이미 존재합니다.");
        }
        messageIds.add(message.getId());
        message.addUser(this);
    }

    public void removeMessage(Message message) {
        if (!messageIds.contains(message.getId())) {
            throw new IllegalArgumentException("해당 메세지는 유저가 쓴 메세지에 존재하지 않습니다.");
        }
        messageIds.remove(message.getId());
        message.removeUser(this);
    }

    // 상태
    public void setToOnline() {
        state = AccountState.ACTIVE;
    }

    public void setToDeleted() {
        state = AccountState.DELETED;
    }

    public void removeAllMessages() {
        messageIds.clear();
    }
}
