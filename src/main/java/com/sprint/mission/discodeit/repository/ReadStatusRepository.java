package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository { // 메세지를 읽은 시간을 표현함
    // ReadStatusRepository CRUD
    ReadStatus save(ReadStatus readStatus);
    Optional<ReadStatus> findById(UUID id);
    List<ReadStatus> findAllByUserId(UUID userId);
    List<ReadStatus> finaAllByChannelId(UUID channelId);
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
