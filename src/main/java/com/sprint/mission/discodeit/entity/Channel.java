package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.domain.ChannelType;
import com.sprint.mission.discodeit.domain.UserType;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Channel extends BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String channelName;
    private final UUID hostUserId;
    private ChannelType.ChannelStatus channelStatus;

    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public Channel(String name, UUID hostUserId) {
        super();
        this.channelName = name;
        this.hostUserId = hostUserId;
        this.channelStatus = ChannelType.ChannelStatus.ACTIVE;
    }

    // getter/setter
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public UUID getHostUserId() {
        return hostUserId;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public ChannelType.ChannelStatus getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(ChannelType.ChannelStatus channelStatus) {
        this.channelStatus = channelStatus;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    // toString
    @Override
    public String toString() {
        return "Channel{" +
                "channelName='" + channelName + '\'' +
                ", hostUserId=" + hostUserId +
                ", channelStatus=" + channelStatus +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                "}\n";
    }

    // 유저 관련 메서드
    // 채널의 유저 리스트에 매개변수로 받은 user 추가
    public void addUserToActiveChannel(User user) {
        // 채널 유효성 검사
        if (channelStatus != ChannelType.ChannelStatus.ACTIVE) {
            System.out.println("<실패: 활성화된 채널이 아닙니다.>");
            return;
        }
        // 유저 유효성 검사
        if (users.contains(user) || user.getStatus() != UserType.UserStatus.ACTIVE) {
            System.out.println("<실패: 이미 가입된 유저거나 온라인 상태가 아닙니다.>");
        }
        users.add(user);
    }

    // 메시지 서비스에서 생성된 메시지를 리스트에 추가함
    public void addMessage(Message message) {
        Optional<Message> nullableMessage = Optional.ofNullable(message);
        nullableMessage.ifPresent(m -> {
            if (messages.contains(m)) {
                System.out.println("<실패: 보낸 메시지입니다.>");
            }
            messages.add(message);
        });
    }

    // 메시지 아이디로 메시지 삭제
    public void removeMessageByMessageIdFromActiveChannel(UUID messageId) {
        if (channelStatus != ChannelType.ChannelStatus.ACTIVE) {
            return;
        }
        messages.removeIf(m -> m.getId().equals(messageId));
    }

    // 동시성 코드: 유저 관련 정보 삭제
    // 메시지 목록에서 특정 유저가 보낸 메시지만 삭제
    public void removeMessagesByUserIdFromActiveChannel(UUID userId) {
        if (channelStatus != ChannelType.ChannelStatus.ACTIVE) {
            return;
        }
        List<Message> removedMessages = messages.stream().filter(m -> m.getUser().getId() != userId).collect(Collectors.toList());
        setMessages(removedMessages);
    }

    // 유저 목록에서 특정 유저만 삭제
    public void removeUserByUserIdFromActiveChannel(UUID userId) {
        if (channelStatus != ChannelType.ChannelStatus.ACTIVE) {
            return;
        }
        List<User> removedUsers = users.stream().filter(u -> u.getId() != userId).collect(Collectors.toList());
        setUsers(removedUsers);
    }

    // 채널 삭제 시 실행: 유저의 채널 정보, 채널 정보 삭제
    public void removeAllUsersAndMessagesFromActiveChannel() {
        if (channelStatus != ChannelType.ChannelStatus.ACTIVE) {
            return;
        }
        for (User user : users) {
            // 유저에서 채널 정보 삭제(채널, 메시지)
            user.removeMessageByChannelId(super.getId());
            user.removeChannelByChannelId(super.getId());
        }
        // 유저, 메시지 비우기
        users.clear();
        messages.clear();
        setUpdatedAt(System.currentTimeMillis());
        setChannelStatus(ChannelType.ChannelStatus.ACTIVE);
    }
}
