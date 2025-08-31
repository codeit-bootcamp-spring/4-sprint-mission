package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Status;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

/********************************************
 * 유저 서비스 인터페이스 구현체
 * CRUD 실행
 * 2025.05.30 김민수
 ********************************************/
public class JCFUserService implements UserService {
    private MessageService jcfMessageService;

    private final List<User> data;

    public JCFUserService(MessageService jcfMessageService) {
        data = new ArrayList<>();
        this.jcfMessageService = jcfMessageService;
    }

    // 유저 생성
    @Override
    public User createUser(String name, String password, String email) {
        User user = new User(name, password, email);
        boolean emailMatch = data.stream().
                anyMatch(user1 -> user1.getEmail().equals(email));
        if (!emailMatch) {
            data.add(user);
            user.setActive(true);
        }
        return user;
    }

    // 모든 유저 확인
    @Override
    public List<User> getUsers() {
        return data;
    }

    // 특정 ID를 가진 유저 가져오기
    @Override
    public Optional<User> getUsersById(UUID userId) {
        Optional<User> user = data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
        return user;
    }

    /********************************************
     * 유저 정보 업데이트
     * @param userId  유저의 아이디 / 수정되지 않음 / 유저를 찾는 용도
     * @param updateUser 수정할 정보를 담은 User / null이 아닌 값을 기존 유저의 값을 수정
     ********************************************/
    @Override
    public void updateUser(UUID userId, User updateUser) {
        Optional<User> target = getUsersById(userId);
        if (target.isPresent()) {
            User user = target.get();
            if (updateUser.getUserName() != null) {
                user.setUserName(updateUser.getUserName());
            }
            if (updateUser.getPassword() != null) {
                user.setPassword(updateUser.getPassword());
            }
            if (updateUser.getEmail() != null) {
                user.setEmail(updateUser.getEmail());
            }
            if (updateUser.getStatus() != null) {
                user.setStatus(updateUser.getStatus());
            }
            if (updateUser.getUserName() != null || updateUser.getPassword() != null || updateUser.getStatus() != null) {
                user.setUpdatedAt(System.currentTimeMillis());
            }
        }
    }

    /********************************************
     *  유저 삭제 메서드
     * @param userId 아이디를 입력으로 받음
     ********************************************/
    @Override
    public void deleteUser(UUID userId) {
        Optional<User> us = data.stream().filter(u -> u.getId().equals(userId)).findFirst();
        if (us.isPresent()) {
            User user = us.get();
            data.remove(user);
            /********************************************
             * 유저가 있던 채널에서 유저 삭제
             ********************************************/
            user.getChannels().stream()
                    .forEach(channel -> {
                        channel.getUsers().remove(user);
                        channel.setUpdatedAt(System.currentTimeMillis());
                    });
            // 모든 채널 내 해당 유저 삭제

            /********************************************
             * 유저가 작성한 메시지를 전체 메시지 내역에서 삭제
             ********************************************/
            user.getMessages().stream()
                    .forEach(message -> {
                        jcfMessageService.removeMessage(message);
                        message.setUpdatedAt(System.currentTimeMillis());
                    });
            // 해당 유저의 모든 대화 내역 삭제
        }
    }

    /**
     * 채널 나가기
     * 유저가 채널을 나가서 삭제되거나 채널이 유저를 퇴장해서 채널이 삭제될 때 사용
     * @param user
     * @param channel
     */
    public void leaveChannel(User user, Channel channel) {
        if(data.contains(user)) {
            user.getMessages().stream()
                    .filter(message -> message.getChannel().equals(channel))
                    .forEach(message -> {
                        jcfMessageService.removeMessage(message);
                        message.setUpdatedAt(System.currentTimeMillis());
                    });
            user.getChannels().remove(channel);
            channel.removeUser(user);
        }
    }
}
