package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReadStatusRepository {

  public ReadStatusEntity save(ReadStatusEntity readStatusEntity);

  public void delete(UUID id);

  public List<ReadStatusEntity> findAll();

  public Optional<ReadStatusEntity> findById(UUID id);
}
