package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserService extends ErrorMessageService implements UserService {

    private static final List<User> users = new ArrayList<>();

    // Create
    @Override
    public Optional<User> addUser(String name) {
        /*
            유저 생성 및 조건 검사
            1. 유저의 이름은 null이거나 비어있으면 안된다.
            위의 조건을 어기면 Optional.empty()가 반환된다.
        */
        if (name != null && !name.trim().isEmpty()) {
            User user = new User(name);
            users.add(user);
            user.setStatus(User.Status.ACTIVE);
            return Optional.of(user);
        } else {
            System.out.println("<addUser 실패: 이름이 비어있습니다. default 상태로 생성합니다.>");
            return Optional.empty();
        }
    }

    // default user 생성기
    @Override
    public User temp() {
        User user = new User("guest");
        users.add(user);
        user.setStatus(User.Status.ACTIVE);
        return user;
    }

    // Read
    @Override
    public List<User> getUsers() {
        if (users.isEmpty()) {
            System.out.println("<유저 리스트가 비어있습니다.>");
            return users;
        } else {
            return users;
        }
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        // 리스트(users)에서 매개변수로 지정된 id와 같고, QUIT(탈퇴) 상태가 아닌 유저만 찾는다.
        Optional<User> user = users.stream()
                .filter(u -> u.getId().equals(id))
                .filter(u -> u.getStatus() != User.Status.QUIT)
                .findFirst();
        if (user.isPresent()) {
            return user;
        } else {
            printErrorMessage();
            return Optional.empty();
        }
    }

    // Update: ACTIVE 유저만 가능
    @Override
    public void updateUserById(UUID id, String name) {
        Optional<User> u = getUserById(id);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getStatus() == User.Status.ACTIVE) {
                user.setUserName(name);
                user.setUpdatedAt(System.currentTimeMillis());
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
            }
        } else {
            // null(empty) 유저에 대한 에러 메시지
            printErrorMessage();
        }
    }

    @Override
    public void updateUserStatusById(UUID id, User.Status status) {
        Optional<User> u = getUserById(id);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getStatus() == User.Status.ACTIVE) {
                user.setStatus(status);
                user.setUpdatedAt(System.currentTimeMillis());
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
            }
        } else {
            // null(empty) 유저에 대한 에러 메시지
            printErrorMessage();
        }
    }

    // Delete: ACTIVE 유저만 가능
    @Override
    public void deleteUserById(UUID id) {
        Optional<User> u = getUserById(id);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getStatus() == User.Status.ACTIVE) {
                user.removeAllChannelsAndMessages();
                user.setStatus(User.Status.QUIT);
                user.setUpdatedAt(System.currentTimeMillis());
                users.remove(user);
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
            }
        } else {
            // QUIT(empty) 유저에 대한 에러 메시지
            printErrorMessage();
        }
    }

    // 채널 참가 관련 메서드: ACTIVE 유저만 가능
    public void joinChannel(UUID id, Channel nullableChannel) {
        Optional<User> u = getUserById(id);
        User user;
        // 유저 검증 (null, ACTIVE 검사)
        if (u.isPresent()) {
            if (u.get().getStatus() == User.Status.ACTIVE) {
                user = u.get();
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
                return;
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
            return;
        }

        // 채널 검증 (null, isActive 검사)
        Optional<Channel> c = Optional.ofNullable(nullableChannel);
        if (c.isPresent()) {
            Channel channel = c.get();
            if (channel.getIsActive()) {
                user.addChannel(channel);
                channel.addUser(user);
            } else {
                // 비활성 상태인 채널에 대한 에러 메시지
                printErrorMessage();
                System.out.println(" <채널 받아오기 실패; isActvie == false>");
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
            System.out.println("<채널 받아오기 실패; channel is null>\")");
        }
    }

    // 채널 나가기: ACTIVE 유저만 가능
    public void outChannel(UUID id, Channel nullableChannel) {
        Optional<User> u = getUserById(id);
        User user;
        // 유저 검증 (null, ACTIVE 검사)
        if (u.isPresent()) {
            if (u.get().getStatus() == User.Status.ACTIVE) {
                user = u.get();
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
                return;
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
            return;
        }

        // 채널 검증 (null, isActive 검사)
        Optional<Channel> c = Optional.ofNullable(nullableChannel);
        if (c.isPresent()) {
            Channel channel = c.get();
            if (channel.getIsActive()) {
                user.outFromChannel(channel);
            } else {
                // 비활성 상태인 채널에 대한 에러 메시지
                printErrorMessage();
                System.out.println(" <채널 받아오기 실패; isActvie == false>");
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
            System.out.println("<채널 받아오기 실패; channel is null>\")");
        }
    }

    // 활성 회원 관련 코드
    // 유저 활성화: BANNED, SLEEP 유저만 가능
    @Override
    public void activateUserById(UUID id) {
        Optional<User> u = getUserById(id);
        if (u.isPresent()) {
            User user = u.get();
            switch (user.getStatus()) {
                case ACTIVE -> {
                    System.out.println("<activateUserById 실패: 이미 활성화된 유저입니다.>");
                }
                case QUIT -> {
                    System.out.println("<activateUserById 실패: 탈퇴한 회원입니다.>");
                }
                case BANNED, SLEEP -> {
                    user.setStatus(User.Status.ACTIVE);
                }
                default -> {
                    System.out.println("<activateUserById 실패: 알 수 없는 상태입니다.>");
                }
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
        }
    }

    // 유저 비활성화 하기: ACTIVE 유저만 가능
    @Override
    public void deactivateUserById(UUID id) {
        Optional<User> u = getUserById(id);
        if (u.isPresent()) {
            User user = u.get();
            if (user.getStatus() == User.Status.ACTIVE) {
                user.setStatus(User.Status.SLEEP);
                user.setUpdatedAt(System.currentTimeMillis());
            } else {
                // BANNED, SLEEP, QUIT 에러 메시지
                printErrorMessage();
            }
        } else {
            // null(empty)에 대한 에러 메시지
            printErrorMessage();
        }
    }
}
