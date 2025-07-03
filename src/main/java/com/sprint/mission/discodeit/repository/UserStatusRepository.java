package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    void createUserStatus(UserStatus userStatus);

    List<UserStatus> loadUserStatuses();
    void saveStatuses(List<UserStatus> userStatuses);

    void deleteUserStatusByUserStatusId (UUID userStatusId);
    void deleteUserStatusByUserId (UUID userId);

    Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId);
    Optional<UserStatus> findUserStatusByUserId (UUID userId);

    void updateUserStatus(UserStatus userStatus);
}