package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class FileUserService implements UserService{
    private static final FileUserService instance = new FileUserService();
    private final ArrayList<User> data;

    public FileUserService() {
        this.data = loadUsers();
    }
    public ArrayList<User> getUsers() {
        return data;
    }
    public static FileUserService getInstance() {
        return instance;
    }

    public void saveUsers() {
        System.out.println("유저 리스트 저장");
        String filePath = "./data/users.ser";

        // ArrayList<User> 직렬화및 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(data);
            System.out.println("User 리스트가 직렬화되어 '" + filePath + "' 파일에 저장되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<User> loadUsers() {
        ArrayList<User> deserializedUsers = null;
        System.out.println("유저 리스트 불러오기");
        String filePath = "./data/users.ser"; // 유저 직렬화

        if (!new File(filePath).exists() || new File(filePath).length() == 0) {
            return new ArrayList<User>(); // 해당 파일이 없으면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            deserializedUsers = (ArrayList<User>) ois.readObject();

            /*
            System.out.println("역직렬화된 User 리스트 정보:");
            for (User user : deserializedUsers) {
                System.out.println("------------");
                System.out.println("User: " + user.toString());
            }*/

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deserializedUsers;
    }

    @Override
    public User createUser(String userName) {
        User user = new User(userName); // 직접 생성
        System.out.printf("유저 추가 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
        data.add(user);
        saveUsers();
        return user;
    }

    @Override
    public void updateUser(User user, String newName) {
        System.out.println("유저의 이름을 변경합니다.");
        System.out.printf("변경 전: '%s', 변경 후: '%s' \n", user.getUserName(), newName);
        user.setUserName(newName);
        saveUsers(); // 데이터를 바꿀 때 마다 저장 -> 비효율적이라 생각, 나중에 질문
    }

    @Override
    public void deleteUser(User user) {
        Optional<Channel> hostChannel = user.getChannels().stream()
                .filter(channel -> channel.getHostUser().equals(user))
                .findFirst();

        if (hostChannel.isPresent()) {
            System.out.printf("'%s'는 '%s' 채널의 호스트이므로 유저를 삭제할 수 없습니다.\n", user.getUserName(), hostChannel.get().getChannelName());
            return; //메서드 종료
        }

        System.out.printf("유저 삭제 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
        // 직접적으로 유저를 사제하진 않고 유저의 status를 false로 바꾼다.
        user.setStatus(UserStatus.DEACTIVE);
        // 유저를 삭제하면 유저가 가지고 있는 모든 채널리스트를 clear
        // 유저를 삭제하면 유저가 가지고 있는 모든 메시지들을 초기화
        // 모든 메시지들을 가지고 있는 채널들에선 해당 메세지를 빼야한다.
        user.getChannels().stream()
                .map(channel -> {
                    channel.removeUser(user);
                    return channel;
                });
        user.clearChannels();
        // 해당 유저가 채널의 주인이면 유저를 삭제할 수 없어야 한다.
        user.getMessages().stream()
                .map(message -> {
                    message.getChannel().removeMessage(message); // 메세지들은 자기고 속한 채널들에서 자신을 지워야한다. !!오류가 날 것 같은 코드, 오류가 나면 이 부분을 의심하자
                    return message;
                });
        user.clearMessages();

        FileMessageService.getInstance().saveMessages();
        // 채널과 메세지들도 변경되었으니 변경
    }

    @Override
    public void restoreUser(User user) {
        System.out.printf("'%s' 유저를 복구합니다. %n", user.getUserName());
        user.setStatus(UserStatus.ACTIVE);
        saveUsers();
    }

    @Override
    public void printActiveUsers(){
        // status를 판별하는 식을 추가
        System.out.printf("전체 유저 조회(탈퇴 유저 미포함), 총 유저 수: %d \n", (data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.ACTIVE))
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
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE))).toList().size());
        data.stream()
                .filter(user -> user.getStatus().equals(UserStatus.DEACTIVE)) // getStatus는 boolean 값이므로 equal 생략
                .forEach(user -> System.out.printf("유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId()));
    }

}
