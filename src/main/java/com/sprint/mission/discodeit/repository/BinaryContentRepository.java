package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


public interface BinaryContentRepository {
    UUID save(BinaryContent content);  // 저장 후 ID 반환

    Optional<BinaryContent> findById(UUID id);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
