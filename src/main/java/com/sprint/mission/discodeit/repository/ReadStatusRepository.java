package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

    @Query("SELECT rs FROM ReadStatus rs WHERE rs.user.id = :userId")
    List<ReadStatus> findAllByUserId(UUID userId);

    @Query("SELECT rs FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    List<ReadStatus> findReadStatusByChannelId(UUID channelId);

    @Query("SELECT rs FROM ReadStatus rs WHERE rs.user.id = :id AND rs.channel.id = :channelId")
    Optional<ReadStatus> findReadStatusByUserIdAndChannelId(UUID id, UUID channelId);

    @Query("DELETE FROM ReadStatus rs WHERE rs.user.id = :userId AND rs.channel.id = :channelId")
    void deleteByUserIdAndChannelId(UUID userId, UUID channelId);

    @Modifying
    @Query("DELETE FROM ReadStatus rs WHERE rs.channel.id = :channelId")
    void deleteAllByChannelId(UUID channelId);
}
