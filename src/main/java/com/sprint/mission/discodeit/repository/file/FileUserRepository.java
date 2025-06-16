package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileUserRepository implements UserRepository, Serializable {

    private static final String USER_FILE_PATH = "./data/user.ser";
    private static final FileUserRepository instance = new FileUserRepository();

    public FileUserRepository() {}
    public static FileUserRepository getInstance() {
        return instance;
    }

    public List<User> loadUsers() {
        File file = new File(USER_FILE_PATH);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<User>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE_PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User createUser(String userName) {
        List<User> users = loadUsers();
        User user = new User(userName); // 직접 생성
        users.add(user);
        saveUsers(users); // 유저 생성은 유저한테만 영향을 미치니까 채널과 메시지는 저장하지 않음
        
        return user;
    }

    @Override
    public void updateUser(User user, String newName) {
        List<User> users = loadUsers();
        user.setUserName(newName);

        for (User userFromFile : users) {
            if (userFromFile.getId().equals(user.getId())) {
                userFromFile.setUserName(newName);
                break;
            }
        }

        // 채널에 있는 유저 이름 변경
        List<Channel> channelsFromFile = FileChannelRepository.getInstance().getAllChannels();

        // 채널에 있는 유저의 이름 변경
        channelsFromFile.stream()
                .filter(channel -> user.getChannels().stream()
                        .anyMatch(userChannel -> userChannel.getId().equals(channel.getId())))
                .forEach(channel ->
                        channel.getUsers().stream()
                                .filter(u -> u.getId().equals(user.getId()))
                                .forEach(u -> u.setUserName(newName))
                );

        List<Message> messagesFromFile = FileMessageRepository.getInstance().getMessages();

        messagesFromFile.stream()
                .filter(msg -> user.getMessages().stream()
                        .anyMatch(userMsg -> userMsg.getId().equals(msg.getId())))
                .forEach(msg -> msg.getUser().setUserName(newName));

        //users.forEach(u -> System.out.println(u.getUserName()));

        saveUsers(users);
        FileChannelRepository.getInstance().saveChannels(channelsFromFile);
        FileMessageRepository.getInstance().saveMessages(messagesFromFile);
    }

    @Override
    public void deleteUser(User user) {

        List<User> users = loadUsers();

        Optional<Channel> hostChannel = user.getChannels().stream()
                .filter(channel -> channel.getHostUser().equals(user))
                .findFirst();

        if (hostChannel.isPresent()) {
            //System.out.printf("'%s'는 '%s' 채널의 호스트이므로 유저를 삭제할 수 없습니다.\n", user.getUserName(), hostChannel.get().getChannelName());
            return; //메서드 종료
        }
        //System.out.printf("유저 삭제 \n유저 이름: '%s', 유저 ID: '%s'\n", user.getUserName(), user.getId());
        // 직접적으로 유저를 삭제하진 않고 유저의 status를 false로 바꾼다.
        for (User userFromFile : users) {
            if (userFromFile.getId().equals(user.getId())){
                // 파일내에 있는 유저를 찾는 과정
                userFromFile.setStatus(UserStatus.DEACTIVE);
                userFromFile.getChannels().stream()
                        .map(channel -> {
                            channel.removeUser(userFromFile);
                            return channel;
                        });
                userFromFile.clearChannels();

                userFromFile.getMessages().stream()
                        .map(message -> {
                            message.getChannel().removeMessage(message); // 메세지들은 자기고 속한 채널들에서 자신을 지워야한다.
                            return message;
                        });
                userFromFile.clearMessages();
                break;
            }
        }
        List<Channel> channelsFromFile = FileChannelRepository.getInstance().getAllChannels();

        user.getChannels().forEach(userChannel ->
                channelsFromFile.stream()
                        .filter(fileChannel -> fileChannel.getId().equals(userChannel.getId()))
                        .forEach(matchedChannel ->
                                matchedChannel.getUsers().removeIf(u -> u.getId().equals(user.getId()))
                        )
        );

        List<Message> messagesFromFile = FileMessageRepository.getInstance().getMessages();

        messagesFromFile.removeIf(messageFromFile ->
                user.getMessages().stream()
                        .anyMatch(messageFromUser -> messageFromUser.getId().equals(messageFromFile.getId()))
        );

        saveUsers(users);
        FileChannelRepository.getInstance().saveChannels(channelsFromFile);
        FileMessageRepository.getInstance().saveMessages(messagesFromFile);
    }

    @Override
    public void restoreUser(User user) {
        List<User> users = loadUsers();
        for (User targetUserInFile : users) {
            if (targetUserInFile.getId().equals(user.getId())){
                targetUserInFile.setStatus(UserStatus.ACTIVE);
                break;
            }
        }
        saveUsers(users);
    }

    @Override
    public List<User> getUsers() {
        return loadUsers();
    }

}
