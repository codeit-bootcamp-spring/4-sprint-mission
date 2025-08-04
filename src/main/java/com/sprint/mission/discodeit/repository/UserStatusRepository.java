package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {
    void deleteById(UUID id);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM user_statuses WHERE user_id = :userId")
    void deleteByUserId(@Param("userId") UUID userId);

    Optional<UserStatus> findById(UUID userId);

    @Query(nativeQuery = true, value = "SELECT * FROM user_statuses WHERE user_id = :userId")
    UserStatus findByUserId(@Param("userId") UUID userId);

    UserStatus save(UserStatus userStatus);

    List<UserStatus> findAll();

    boolean existsByUser_Id(UUID userId);
}
