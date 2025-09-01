package com.codeit.discodeit8.repository;

import com.codeit.discodeit8.entity.UserStatus;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUserId(UUID userId);
}