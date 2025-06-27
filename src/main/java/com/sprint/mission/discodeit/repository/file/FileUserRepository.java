package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository, Serializable {

    private final String filePath;

    public FileUserRepository(String fileDirectory) {
        this.filePath = fileDirectory + "/user.ser";
    }

    @Override
    public List<User> loadUsers() {
        File file = new File(filePath);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<User>(); // 해당 파일이 없거나 비어 있다면 빈 ArrayList를 반환
        }
        // ArrayList<User> 역직렬화및 반환
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public void saveUsers(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createUser(User user) {
        List<User> users = loadUsers();
        users.add(user);
        saveUsers(users); // 유저 생성은 유저한테만 영향을 미치니까 채널과 메시지는 저장하지 않음
    }

    @Override
    public void updateUser(User user) {
        List<User> users = loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).equalsId(user)) {
                users.set(i, user); // 리스트 내부 요소를 직접 교체
                saveUsers(users);
                return;
            }
        }
    }

    @Override
    public void deleteUser(User user) {
        List<User> usersFromFile = loadUsers();

        for (User userFromFile : usersFromFile) {
            if (userFromFile.equalsId(user)) {
                userFromFile.setStatus(UserActivationState.DEACTIVE);
                userFromFile.clearChannelIds();
                //usersFromFile.remove(userFromFile);
                break; // 유저 찾았으니 루프 종료
            }
        }

        saveUsers(usersFromFile);
    }

    @Override
    public void restoreUser(String userName) {
        List<User> users = loadUsers();
        for (User targetUserInFile : users) {
            if (targetUserInFile.getUserName().equals(userName)){
                targetUserInFile.setStatus(UserActivationState.ACTIVE);
                break;
            }
        }
        saveUsers(users);
    }


    @Override
    public Optional<User> findUserById(UUID userId) {
        return loadUsers().stream()
                .filter(u -> u.equalsId(userId))
                .findFirst();
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return loadUsers().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<User> findUserByUserName(String username){
        return loadUsers().stream()
                .filter(user -> user.getUserName().equals(username))
                .findFirst();
    }

    @Override
    public Optional<User> findUserByUserId(UUID userId){
        return loadUsers().stream()
                .filter(user -> user.equalsId(userId))
                .findFirst();
    }
}