package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  UserEntity save(UserEntity userEntity);

  Optional<UserEntity> findById(UUID userId);

  List<UserEntity> findAll();

  void delete(UUID id);

  List<UserEntity> findUsersByNameContains(String name);

  Optional<UserEntity> findByUsername(String username);
}
