package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    List<ReadStatus> findAll();

    ReadStatus save(ReadStatus readStatus);

    void deleteById(UUID id);

    @Query(nativeQuery = true, value = "SELECT * FROM read_statuses WHERE channel_id = :channelId")
    List<ReadStatus> findByChannelId(@Param("channelId") UUID channelId);

    @Query(nativeQuery = true, value = "SELECT * FROM read_statuses WHERE user_id = :userId")
    List<ReadStatus> findAllByUserId(@Param("userId") UUID userId);

    boolean existsByUser_IdAndChannel_Id(UUID userId, UUID channelId);

    List<ReadStatus> findAllByUser_Id(UUID userId);
}