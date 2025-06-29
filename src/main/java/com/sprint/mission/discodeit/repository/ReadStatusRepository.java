package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ReadStatusRepository {

    UUID save(ReadStatus readStatus);

    ReadStatus findById(UUID Id);

    List<ReadStatus> findByUserId(UUID Id);

    List<ReadStatus> findByChannelId(UUID channelId);

    List<ReadStatus> findAll();

    void deleteByUserId(UUID userId);

    void deleteByChannelId(UUID channelId);

    void deleteById(UUID Id);

    boolean existsByUserId(UUID userId);
}
