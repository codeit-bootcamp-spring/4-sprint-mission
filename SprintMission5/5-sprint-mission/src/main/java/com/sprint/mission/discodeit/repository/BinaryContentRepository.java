package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BinaryContentRepository {

  public BinaryContentEntity save(BinaryContentEntity binaryContentEntity);

  public Optional<BinaryContentEntity> findById(UUID id);

  public List<BinaryContentEntity> findAll();

  public void delete(UUID id);
}
