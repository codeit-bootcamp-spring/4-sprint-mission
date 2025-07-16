package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.AccountState;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FileUserRepository implements UserRepository {

  private final Map<UUID, UserEntity> userCache = new ConcurrentHashMap<>();
  private final File userDir;

  public FileUserRepository(String baseDirectory) {
    this.userDir = new File(baseDirectory, "User");
    if (!userDir.exists()) {
      if (!userDir.mkdirs()) {
        throw new RuntimeException("User 디렉토리 생성 실패: " + userDir.getAbsolutePath());
      }
    }
    loadAllUsersToCache();
  }

  private void saveUserToFile(UserEntity userEntity) {
    File file = new File(userDir, userEntity.getId().toString() + ".ser");
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
      oos.writeObject(userEntity);
    } catch (IOException e) {
      throw new RuntimeException("유저 저장 실패: " + e.getMessage(), e);
    }
  }

  private void loadAllUsersToCache() {
    File[] files = userDir.listFiles((dir, name) -> name.endsWith(".ser"));
    if (files == null) {
      return;
    }

    for (File file : files) {
      try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
        UserEntity user = (UserEntity) ois.readObject();
        userCache.put(user.getId(), user);
      } catch (IOException | ClassNotFoundException e) {
        System.err.println("유저 로딩 실패: " + e.getMessage());
      }
    }
  }

  @Override
  public UserEntity save(UserEntity userEntity) {
    userCache.put(userEntity.getId(), userEntity);
    saveUserToFile(userEntity);
    return userEntity;
  }

  @Override
  public Optional<UserEntity> findById(UUID userId) {
    UserEntity user = userCache.get(userId);
    if (user == null || user.getState() == AccountState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(user);
  }

  @Override
  public List<UserEntity> findAll() {
    return userCache.values().stream()
        .filter(user -> user.getState() != AccountState.DELETED)
        .toList();
  }

  @Override
  public void delete(UUID id) {
    UserEntity user = userCache.get(id);
    if (user != null && user.getState() != AccountState.DELETED) {
      user.setToDeleted();
      saveUserToFile(user);
    }
  }

  @Override
  public List<UserEntity> findUsersByNameContains(String name) {
    return userCache.values().stream()
        .filter(user -> user.getUsername().contains(name))
        .filter(user -> user.getState() != AccountState.DELETED)
        .toList();
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return userCache.values().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst();
  }
}
