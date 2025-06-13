package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserState;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
  private final Map<UUID, User> data = new ConcurrentHashMap<>();

  @Override
  public User save(User user) {
    data.put(user.getUserId(), user);
    return user;
  }

  @Override
  public Optional<User> findById(UUID userId) {
    User user = data.get(userId);
    if (user == null || user.getState() == UserState.DELETED) {
      return Optional.empty();
    }
    return Optional.of(user);
  }

  @Override
  public List<User> findAll() {
    return data.values().stream()
        .filter(user -> user.getState() != UserState.DELETED)
        .collect(Collectors.toList());
  }

  @Override
  public void delete(User user) {
    User existing = data.get(user.getUserId());
    if (existing != null && existing.getState() != UserState.DELETED) {
      existing.deletedUserState();
    }
  }

  @Override
  public List<User> findUsersByNameContains(String name) {
    return data.values().stream()
        .filter(user -> user.getUserName().contains(name))
        .filter(user -> user.getState() != UserState.DELETED)
        .collect(Collectors.toList());
  }
}
