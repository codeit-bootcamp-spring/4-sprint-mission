package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.Optional;

public class JCFUserRepository implements UserRepository {

    private static final String CHANNEL_FILE_PATH = "./data/channels.ser";
    private static final JCFUserRepository instance = new JCFUserRepository();
    private final ArrayList<User> data;

    public JCFUserRepository() {
        data = new ArrayList<>();
    }

    public static JCFUserRepository getInstance() {
        return instance;
    }

    @Override
    public User createUser(String userName) {
        User user = new User(userName); // 직접 생성
        System.out.printf("유저 추가 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName() ,user.getId());
        data.add(user);
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

        user.setStatus(UserStatus.DEACTIVE);
        user.getChannels().stream()
                .map(channel -> {
                    channel.removeUser(user);
                    return channel;
                });
        user.clearChannels();
        user.getMessages().stream()
                .map(message -> {
                    message.getChannel().removeMessage(message); 
                    return message;
                });
        user.clearMessages();
        // 상세한 주석은 service에
    }

    @Override
    public void restoreUser(User user) {
        System.out.printf("'%s' 유저를 복구합니다. %n", user.getUserName());
        user.setStatus(UserStatus.ACTIVE);
    }

    public void printAllUsers() {
        System.out.printf("전체 유저 조회(탈퇴 유저 포함), 유저 수: %d \n", data.size());
        data
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
        //제대로 레포지터리가 작동하는지를 확인하기위한 메소드
    }

    @Override
    public ArrayList<User> getUsers() {
        return data;
    }
}
