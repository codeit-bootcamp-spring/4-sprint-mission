package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {
    public List<ReadStatus> findAllByChannelId(UUID channelId);

    public List<ReadStatus> findAllByUserId(UUID userId);

    public List<ReadStatus> findAllByChannelIdAndUserId(UUID channelId, UUID userId);

    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);
}
