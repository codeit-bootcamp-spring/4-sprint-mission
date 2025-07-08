package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository { // UserStatus CRUD
    public UserStatus save(UserStatus userStatus);
    public Optional<UserStatus> findUserStatus(UUID id);
    public List<UserStatus> findAll();
    public boolean existsId(UUID id);
    public void deleteUserStatus(UUID id);
}
