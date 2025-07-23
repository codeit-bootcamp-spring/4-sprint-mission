package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusRepository {
    void delete(UUID id);
    void deleteByUserId(UUID userId);
    UserStatus findById(UUID userId);
    UserStatus findByUserId(UUID userId);
    void save(UserStatus userStatus);
    List<UserStatus> findAll();
}
