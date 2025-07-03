package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Setter
@Getter
public class User extends BaseEntity implements Serializable {

    private String userName;
    private String password;
    private String email;
    private UUID profileId; // 프로필 사진에 대한 것으로 BinaryContents를 참조하기 위한 필드

    private final Set<UUID> channelIds = new HashSet<>();
    private final Set<UUID> messageIds = new HashSet<>();

    @Setter
    private UserActivationState status = UserActivationState.ACTIVE;

    public User(String userName, String password, String email, UUID profileId) {
        super();
        this.userName = userName;
        this.password = password;
        this.userName = userName;
        this.email = email;
        this.profileId = profileId;
    }

    public void addChannel(Channel channel) {
        if (!channelIds.add(channel.getId())) {
            // 값이 추가되면 true 반환함 따라서 추가 안되면 메시지를 발행
           // System.out.println("Channel: " + channel.getId() + ", already exists");
            return; // 순환참조를 방지하기위한 메소드 종료
        }
        channel.addUser(this);
    }

    public void removeChannel(Channel channel) {
        if  (!channelIds.remove(channel.getId())) {
           // System.out.println("Channel: " + channel.getId() + ", isn't exists");
            return;
        }
        channel.removeUser(this);
    }

    public void addMessage(Message message) {
        if (!messageIds.add(message.getId())) {
           // System.out.println("Message: " + message.getId() + ", already exists");
        }
    }

    public void removeMessage(Message message) {
        if (!messageIds.remove(message.getId())) {
            //System.out.println("Message: " + message.getId() + ", isn't exists");
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
        updateUpdatedAt();
    }

    public void clearChannelIds() {
        channelIds.clear();
    }

    public void clearMessageIds() {
        messageIds.clear();
    }
}