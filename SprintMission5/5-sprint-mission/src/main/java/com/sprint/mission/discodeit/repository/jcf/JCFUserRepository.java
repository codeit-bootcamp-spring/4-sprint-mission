package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.AccountState;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {

  private final Map<UUID, UserEntity> data = new ConcurrentHashMap<>();

  @Override
  public UserEntity save(UserEntity userEntity) {
    data.put(userEntity.getId(), userEntity);
    return userEntity;
  }

  @Override
  public Optional<UserEntity> findById(UUID userId) {
    UserEntity userEntity = data.get(userId);
    if (userEntity == null || userEntity.getState() == AccountState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(userEntity);
  }

  @Override
  public List<UserEntity> findAll() {
    return data.values().stream()
        .filter(user -> user.getState() != AccountState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(UUID id) {
    UserEntity existing = data.get(id);
    if (existing != null && existing.getState() != AccountState.DELETED) {
      existing.setToDeleted();
    }
  }

  @Override
  public List<UserEntity> findUsersByNameContains(String name) {
    return data.values().stream()
        .filter(user -> user.getUsername().contains(name))
        .filter(user -> user.getState() != AccountState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return data.values().stream()
        .filter(user -> user.getUsername().equals(username))
        .findFirst();
  }
}
