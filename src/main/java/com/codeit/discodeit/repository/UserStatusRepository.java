package com.codeit.discodeit.repository;

import com.codeit.discodeit.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository {
    void createUserStatus(UserStatus userStatus);

    void updateUserStatus(UserStatus userStatus);

    Optional<UserStatus> findUserStatusByUserStatusId(UUID userStatusId);

    Optional<UserStatus> findUserStatusByUserId (UUID userId);

}