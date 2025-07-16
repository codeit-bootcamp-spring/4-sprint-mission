package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatusEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {

  UserStatusEntity save(UserStatusEntity status);

  Optional<UserStatusEntity> findById(UUID id);

  List<UserStatusEntity> findAll();

  void delete(UUID id);

  Optional<UserStatusEntity> findByUserId(UUID userId);
}
