package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class Channel extends BaseEntity {
    private String channelName;
    private final UUID hostUserId;
    private boolean isActive;

    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public Channel(String name, UUID hostUserId) {
        super();
        this.channelName = name;
        this.hostUserId = hostUserId;
    }

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

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", channelName='" + channelName + '\'' +
                ", hostUserId=" + hostUserId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}' + "\n";
    }

    // 유저 관련 메서드
    // 채널의 유저 리스트에 매개변수로 받은 user 추가
    public void addUser(User user) {
        // 채널 유효성 검사
        if (!isActive) {
            System.out.println("<실패: 활성화된 채널이 아닙니다.>");
            return;
        }
        // 유저 유효성 검사
        if (users.contains(user)) {
            System.out.println("<실패: 이미 가입된 유저입니다.>");
        } else if (user.getStatus() == User.Status.ACTIVE) {
            users.add(user);
        }
    }

    // 메시지 서비스에서 생성된 메시지를 리스트에 추가함
    public void addMessage(Message message) {
        Optional<Message> nullableMessage = Optional.ofNullable(message);
        nullableMessage.ifPresent(m -> {
            if (messages.contains(m)) {
                System.out.println("<실패: 보낸 메시지입니다.>");
            } else {
                messages.add(message);
            }
        });
    }

    // 메시지 아이디로 메시지 삭제
    public void removeMessageByMessageId(UUID messageId) {
        if (!isActive) {
            return;
        }
        messages.removeIf(m -> m.getId().equals(messageId));
    }

    // 동시성 코드: 유저 관련 정보 삭제
    // 메시지 목록에서 특정 유저가 보낸 메시지만 삭제
    public void removeMessagesByUserId(UUID userId) {
        if (!isActive) {
            return;
        }
        List<Message> removedMessages = messages.stream()
                .filter(m -> m.getUser().getId() != userId)
                .collect(Collectors.toList());
        setMessages(removedMessages);
    }

    // 유저 목록에서 특정 유저만 삭제
    public void removeUserByUserId(UUID userId) {
        if (!isActive) {
            return;
        }
        List<User> removedUsers = users.stream()
                .filter(u -> u.getId() != userId)
                .collect(Collectors.toList());
        setUsers(removedUsers);
    }

    // 채널 삭제 시 실행: 유저의 채널 정보, 채널 정보 삭제
    public void removeAllUsersAndMessages() {
        if (!isActive) {
            return;
        }
        for (User user : users) {
            // 유저에서 채널 정보 삭제(채널, 메시지)
            user.removeMessageByChannelId(id);
            user.removeChannelByChannelId(id);
        }
        // 유저, 메시지 비우기
        users.clear();
        messages.clear();
        setIsActive(false);
    }
}
