package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {

    private static final String SERIALIZED_FILE_PATH = "data/users.ser";
    private static final FileUserRepository instance = new FileUserRepository();

    public static FileUserRepository getInstance() {
        return instance;
    }

    private void saveAll(List<User> users) {
        try (FileOutputStream fos = new FileOutputStream(SERIALIZED_FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<User> loadAll() {
        try (FileInputStream fis = new FileInputStream(SERIALIZED_FILE_PATH);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public User save(User user) {
        List<User> users = loadAll();
        users.add(user);
        saveAll(users);
        return user;
    }

    @Override
    public User findById(String userId) {
        return loadAll().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return loadAll();
    }

    @Override
    public User delete(String userId) {
        List<User> users = loadAll();
        User removed  = null;
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                removed = user;
                removed.getMessages().clear();
                users.remove(user);
                break;
            }
        }
        if (removed != null) {
            saveAll(users);
        }
        return removed;
    }
}
