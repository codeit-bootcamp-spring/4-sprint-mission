package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import com.sprint.mission.discodeit.repository.UserStatusRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFUserStatusRepository implements UserStatusRepository {

  private final Map<UUID, UserStatusEntity> data = new ConcurrentHashMap<>();

  @Override
  public UserStatusEntity save(UserStatusEntity status) {
    data.put(status.getId(), status);
    return status;
  }

  @Override
  public Optional<UserStatusEntity> findById(UUID id) {
    return Optional.ofNullable(data.get(id));
  }

  @Override
  public List<UserStatusEntity> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID id) {
    UserStatusEntity removed = data.remove(id);
    if (removed == null) {
      System.err.println("삭제할 UserStatus가 존재하지 않습니다. id=" + id);
    }
  }

  // userId로 UserStatus 조회 (서비스 계층에서 필요)
  @Override
  public Optional<UserStatusEntity> findByUserId(UUID userId) {
    return data.values().stream()
        .filter(us -> us.getUserId().equals(userId))
        .findFirst();
  }
}
