package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatusEntity> userStatusCache = new ConcurrentHashMap<>();
  private final File userStatusDir;

  public FileUserStatusRepository(String baseDirectory) {
    this.userStatusDir = new File(baseDirectory, "UserStatus");
    if (!userStatusDir.exists()) {
      if (!userStatusDir.mkdirs()) {
        throw new RuntimeException("UserStatus 디렉토리 생성 실패: " + userStatusDir.getAbsolutePath());
      }
    }
    loadAllUserStatusesToCache();
  }

  private void saveUserStatusToFile(UserStatusEntity status) {
    File file = new File(userStatusDir, status.getId().toString() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(status);
    } catch (IOException e) {
      throw new RuntimeException("UserStatus 저장 실패: " + e.getMessage(), e);
    }
  }

  private void loadAllUserStatusesToCache() {
    File[] files = userStatusDir.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files == null) {
      return;
    }

    for (File file : files) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        UserStatusEntity status = (UserStatusEntity) ois.readObject();
        userStatusCache.put(status.getId(), status);
      } catch (IOException | ClassNotFoundException e) {
        System.err.println("UserStatus 로딩 실패: " + e.getMessage());
      }
    }
  }

  @Override
  public UserStatusEntity save(UserStatusEntity status) {
    userStatusCache.put(status.getId(), status);
    saveUserStatusToFile(status);
    return status;
  }

  @Override
  public Optional<UserStatusEntity> findById(UUID id) {
    return Optional.ofNullable(userStatusCache.get(id));
  }

  @Override
  public List<UserStatusEntity> findAll() {
    return new ArrayList<>(userStatusCache.values());
  }

  @Override
  public void delete(UUID id) {
    userStatusCache.remove(id);
    File file = new File(userStatusDir, id.toString() + ".ser");
    if (file.exists() && !file.delete()) {
      System.err.println("UserStatus 파일 삭제 실패: " + file.getAbsolutePath());
    }
  }

  @Override
  public Optional<UserStatusEntity> findByUserId(UUID userId) {
    return userStatusCache.values().stream()
        .filter(status -> status.getUserId().equals(userId))
        .findFirst();
  }
}
