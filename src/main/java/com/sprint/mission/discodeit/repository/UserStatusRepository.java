package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface UserStatusRepository {
    void save(UserStatus status);

    Optional<UserStatus> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    List<UserStatus> findAll();
}