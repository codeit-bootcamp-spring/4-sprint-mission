package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.AccountState;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FileUserRepository implements UserRepository {
    private static final String DATA_FILE = "data/user.ser";
    private final Map<UUID, User> userCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileUserRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllUsersToCache();
    }

    private void saveAllUsersToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<User> users = new ArrayList<>(userCache.values());
            oos.writeObject(users);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllUsersToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<User> users = (List<User>) ois.readObject();
            users.forEach(user -> userCache.put(user.getId(), user));
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public User save(User user) {
        userCache.put(user.getId(), user);
        saveAllUsersToFile();
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        User user = userCache.get(userId);
        if (user == null || user.getState() == AccountState.DELETED) {
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        return userCache.values().stream()
                .filter(user -> user.getState() != AccountState.DELETED)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        User cached = userCache.get(id);
        if (cached != null && cached.getState() != AccountState.DELETED) {
            cached.deletedUserState();
            saveAllUsersToFile();
        }
    }

    @Override
    public List<User> findUsersByNameContains(String name) {
        return userCache.values().stream()
                .filter(user -> user.getUserName().contains(name))
                .filter(user -> user.getState() != AccountState.DELETED)
                .collect(Collectors.toList());
    }
}
