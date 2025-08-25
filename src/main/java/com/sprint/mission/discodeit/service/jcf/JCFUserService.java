package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {


    private static JCFUserService instance = new JCFUserService();
    private final ArrayList<User> data;

    public JCFUserService() {
        data = new ArrayList<>();
    }

    public static JCFUserService getInstance() {
        return instance;
    }

    @Override
    public void addUser(User user) {
        System.out.printf("유저 추가 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName() ,user.getId());
        data.add(user);
    }

    @Override
    public User createUser(String userName) {
        User user = new User(userName); // 직접 생성
        addUser(user); // 내부 등록
        return user;
    }

    @Override
    public void updateUser(User user, String newName) {
        System.out.println("유저의 이름을 변경합니다.");
        System.out.printf("변경 전: '%s', 변경 후: '%s' \n", user.getUserName(), newName);
        user.setUserName(newName);
    }

    @Override
    public void deleteUser(User user) {

        Optional<Channel> hostChannel = user.getChannels().stream()
                .filter(channel -> channel.getHostUser().equals(user))
                .findFirst();

        if (hostChannel.isPresent()) {
            System.out.printf("'%s'는 '%s' 채널의 호스트이므로 유저를 삭제할 수 없습니다.\n",user.getUserName(), hostChannel.get().getChannelName());
            return; //메서드 종료
        }

        System.out.printf("유저 삭제 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
        // usersData.remove(user);
        // 직접적으로 유저를 사제하진 않고 유저의 status를 false로 바꾼다.
        user.setStatus(User.UserStatus.DEACTIVE);
        // 유저를 삭제하면 유저가 가지고 있는 모든 채널리스트를 clear
        // 유저를 삭제하면 유저가 가지고 있는 모든 메시지들을 초기화
        // 모든 메시지들을 가지고 있는 채널들에선 해당 메세지를 빼야한다.
        user.getChannels().stream()
                .map(channel ->
                {
                    channel.removeUser(user);
                    return channel;
                });
        user.clearChannels();
        // 해당 유저가 채널의 주인이면 유저를 삭제할 수 없어야 한다.
        user.getMessages().stream()
                .map(message ->
                {
                    message.getChannel().removeMessage(message); // 메세지들은 자기고 속한 채널들에서 자신을 지워야한다.
                    return message;
                });
        user.clearMessages();
    }

    @Override
    public void printActiveUsers(){
        // status를 판별하는 식을 추가
        System.out.printf("전체 유저 조회(탈퇴 유저 미포함), 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(User.UserStatus.ACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(User.UserStatus.ACTIVE))
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printUser(User user) {
        System.out.print("단일 유저 조회\n");
        System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
    }

    @Override
    public void printAllUsers() {
        System.out.printf("전체 유저 조회(탈퇴 유저 포함), 유저 수: %d \n", data.size());
        data
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void printDeactivatedUsers() {
        System.out.printf("탈퇴 유저 조회, 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(User.UserStatus.DEACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(User.UserStatus.DEACTIVE)) // getStatus는 boolean 값이므로 equal 생략
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

    @Override
    public void restorationUser(User user) {
        System.out.printf("'%s' 유저를 복구합니다. %n", user.getUserName());
        user.setStatus(User.UserStatus.ACTIVE);
    }

}
