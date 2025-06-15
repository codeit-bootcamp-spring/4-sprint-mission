package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.file.FileIOHelper;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.nio.file.Path;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private static FileUserRepository instance;
    private final Map<UUID, User> data; // 효율적인 탐색을 위해 List에서 Map으로 변경
    private final Path filePath;

    private FileUserRepository(Path filePath) {
        this.filePath = filePath;
        this.data = FileIOHelper.loadMap(filePath);
    }
    // 최초 초기화 시에만 Path 필요
    public static FileUserRepository getInstance(Path filePath) {
        if (instance == null) {
            instance = new FileUserRepository(filePath);
        }
        return instance;
    }

    // 두 번째 이후 호출 시에는 filePath 없이
    public static FileUserRepository getInstance() {
        if (instance == null) {
            throw new IllegalStateException("FileUserRepository는 아직 초기화되지 않았습니다. getInstance(Path filePath)를 먼저 호출하세요.");
        }
        return instance;
    }

    // 모든 유저 데이터를 파일에 저장
    public void saveAllUsers() {
        FileIOHelper.saveMap(filePath, data);
    }

    @Override
    public User save(User user) {
        data.put(user.getUserId(), user);
        saveAllUsers();
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return data.get(userId);
    }

    @Override
    public List<User> findByName(String userName) {
        List<User> usersByName = new ArrayList<>();
        for (User user : data.values()) {
            if (user.getUserName().equals(userName) && user.getStatus() != User.Status.DELETED) {
                usersByName.add(user);
            }
        }
        return usersByName;
    }

    //모든 유저리스트 반환
    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public boolean isContains(UUID userId) {
        return data.containsKey(userId);
    }
}
