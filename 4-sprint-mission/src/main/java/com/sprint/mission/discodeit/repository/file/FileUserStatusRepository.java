package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserStatusRepository implements UserStatusRepository {
    private static final String DATA_FILE = "data/userStatus.ser";
    private final Map<UUID, UserStatus> userStatusCache = new ConcurrentHashMap<>();
    private final String fileDirectory;

    public FileUserStatusRepository(String fileDirectory) {
        this.fileDirectory = fileDirectory;
        loadAllUserStatusToCache();
    }

    private void saveAllUserStatusToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            List<UserStatus> userStatusList = new ArrayList<>(userStatusCache.values());
            oos.writeObject(userStatusList);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패" + e);
        }
    }

    @SuppressWarnings("unchecked")
    private void loadAllUserStatusToCache() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            System.err.println("데이터 파일이 존재하지 않습니다: " + DATA_FILE);
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            List<UserStatus> userStatusList = (List<UserStatus>) ois.readObject();
            userStatusList.forEach(userStatus -> userStatusCache.put(userStatus.getId(), userStatus));
        } catch (IOException e) {
            System.err.println("파일 입출력 오류 발생: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("클래스를 찾을 수 없습니다: " + e.getMessage());
        }
    }

    @Override
    public UserStatus save(UserStatus status) {
        userStatusCache.put(status.getId(), status);
        saveAllUserStatusToFile();
        return status;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(userStatusCache.get(id));
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(userStatusCache.values());
    }

    @Override
    public void delete(UUID id) {
        UserStatus userStatus = userStatusCache.get(id);
        if (userStatus != null) {
            userStatusCache.remove(id);
            saveAllUserStatusToFile();
        }
    }
}
