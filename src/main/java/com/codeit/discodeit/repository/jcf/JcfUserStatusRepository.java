package com.codeit.discodeit.repository.jcf;

import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.repository.UserStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class JcfUserStatusRepository implements UserStatusRepository, Serializable {

  private List<UserStatus> userStatusData = new ArrayList<>();

  @Override
  public List<UserStatus> loadUserStatuses() {
    return userStatusData;
  }

  @Override
  public void saveStatuses(List<UserStatus> userStatuses) {
    userStatusData = userStatuses;
  }

  @Override
  public void createUserStatus(UserStatus userStatus) {
    List<UserStatus> userStatuses = loadUserStatuses();
    userStatuses.add(userStatus);
    saveStatuses(userStatuses);
  }

  @Override
  public void deleteUserStatusByUserStatusId(UUID userStatusId) {
    List<UserStatus> userStatuses = loadUserStatuses();
    userStatuses.removeIf(userStatus -> userStatus.getId().equals(userStatusId));
    saveStatuses(userStatuses);
  }

  @Override
  public void deleteUserStatusByUserId(UUID userId) {
    List<UserStatus> userStatuses = loadUserStatuses();
    userStatuses.removeIf(userStatus -> userStatus.getUserId().equals(userId));
    saveStatuses(userStatuses);
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserId(UUID userId) {
    List<UserStatus> userStatuses = loadUserStatuses();

    return userStatuses.stream()
        .filter(userStatus -> userStatus.getUserId().equals(userId)).findFirst();
  }

  @Override
  public Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId) {
    List<UserStatus> userStatuses = loadUserStatuses();
    return userStatuses.stream().filter(userStatus -> userStatus.getUserId().equals(userStatusId))
        .findFirst();
  }

  @Override
  public void updateUserStatus(UserStatus userStatus) {
    List<UserStatus> userStatuses = loadUserStatuses();
    for (int i = 0; i < userStatuses.size(); i++) {
      if (userStatuses.get(i).getId().equals(userStatus.getId())) {
        userStatuses.set(i, userStatus);
        saveStatuses(userStatuses);
        return;
      }
    }

  }
}
