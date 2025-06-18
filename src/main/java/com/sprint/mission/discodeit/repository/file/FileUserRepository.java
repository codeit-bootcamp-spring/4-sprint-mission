package com.sprint.mission.discodeit.repository.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class FileUserRepository implements UserRepository {
    private static final String FILE_PATH = "data/users.ser";

    @Override
    public Optional<User> findById(UUID userId) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            List<User> users = (List<User>) ois.readObject();
            return users.stream()
                    .filter(user -> user.getUserId().equals(userId))
                    .findFirst();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return java.util.Optional.empty();
        }
    }

    @Override
    public void save(User user) {
        List<User> users;

        //먼저 기존 파일에서 유저 리스트를 불러옴
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }

        users.removeIf( u -> u.getUserId().equals(user.getUserId()));
        users.add(user);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteById(UUID userId) {
        List<User> users;

        //기존 유저 리스트 불러오기
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            users = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            users = new ArrayList<>();
        }

        // 해당 유저 제거🔪
        users.removeIf( u -> u.getUserId().equals(userId));

        // 수정된 리스트를 저장
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<User> findAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
