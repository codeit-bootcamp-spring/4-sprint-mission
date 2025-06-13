package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserState;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {
    private static final String DATA_FILE = "data/user.ser";
    private final Map<UUID, User> userCache = new ConcurrentHashMap<>();

    public FileUserRepository() {
        loadAllUsersToCache();
    }
    private void saveAllUsersToFile() {
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
    public User save(User user) {
        userCache.put(user.getUserId(), user);
        saveAllUsersToFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        User user = userCache.get(userId);
        if(user==null || user.getState()==UserState.DELETED){
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        return userCache.values().stream()
                .filter(user -> user.getState()!=UserState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(User user) {
        User cached =  userCache.get(user.getUserId());
        if(cached!=null && cached.getState()!=UserState.DELETED){
            cached.deletedUserState();
            saveAllUsersToFile();
        }
    }

    @Override
    public List<User> findUsersByNameContains(String name) {
        return userCache.values().stream()
                .filter(user -> user.getUserName().contains(name))
                .filter(user -> user.getState() != UserState.DELETED)
                .collect(Collectors.toList());
    }
}
