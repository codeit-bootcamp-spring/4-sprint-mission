package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    private static final String DATA_FILE = "data/user.ser";
    private final Map<UUID, User> userCache = new ConcurrentHashMap<>();

    public FileUserService() {
        loadAllUsersToCache();
    }


    private void saveUser() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))){
            List<User> users = new ArrayList<>(userCache.values());
            oos.writeObject(users);
        }catch (IOException e) {
            throw new RuntimeException("저장 실패" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllUsersToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return;
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            List<User> users = (List<User>) ois.readObject();
            users.forEach(user -> userCache.put(user.getUserId(), user));
        }catch (IOException | ClassNotFoundException e){
            System.err.println("불러오기 실패: " + e.getMessage());
        }
    }

    @Override
    public User addUser(String name) {
        User user = new User(name);
        userCache.put(user.getUserId(), user);
        saveUser();
        return user;
    }

    @Override
    public List<User> findAllUser() {
        return userCache.values().stream()
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findUserById(UUID UserId) {
        User user = userCache.get(UserId);
        if(user==null || user.getState()==UserState.DELETED){
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findUsersByNameContains(String UserName) {
        return userCache.values().stream()
                .filter(user -> user.getUserName().contains(UserName))
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void updateUser(UUID userId, String updatedText) {
        User user = userCache.get(userId);
        if(user!=null && user.getState()!=UserState.DELETED){
            user.setUserName(updatedText);
            user.setUpdatedAt();
            saveUser();
        }
    }

    @Override
    public void deleteUser(User user) {
        User cached = userCache.get(user.getUserId());
        if(cached!=null && cached.getState()!=UserState.DELETED) {
            cached.deletedUserState();
            saveUser();
        }
    }

    @Override
    public List<Message> findMessagesByUser(User user) {
        return user.getMessages().stream()
                .filter(message -> message.getState() != MessageState.DELETED)
                .collect(Collectors.toList());
    }

}
