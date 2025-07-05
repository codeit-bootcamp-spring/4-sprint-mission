package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    public UserStatus save(UserStatus status);

    public Optional<UserStatus> findById(UUID id);

    public List<UserStatus> findAll();

    public void delete(UUID id);
}
