package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    UserStatus save(UserStatus status);
    Optional<UserStatus> findByUserId(UUID userId);
    Optional<UserStatus> findById(UUID id);
    List<UserStatus> findAll();
    boolean existsById(UUID id);
    void delete(UUID userid);
}
