package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFReadStatusRepository implements ReadStatusRepository {

  private final Map<UUID, ReadStatusEntity> data = new ConcurrentHashMap<>();

  @Override
  public ReadStatusEntity save(ReadStatusEntity readStatusEntity) {
    data.put(readStatusEntity.getId(), readStatusEntity);
    return readStatusEntity;
  }

  @Override
  public void delete(UUID id) {
    ReadStatusEntity readStatusEntity = data.get(id);
    if (readStatusEntity != null) {
      data.remove(id);
    }
  }

  @Override
  public List<ReadStatusEntity> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public Optional<ReadStatusEntity> findById(UUID id) {
    ReadStatusEntity readStatusEntity = data.get(id);
    if (readStatusEntity == null) {
      return Optional.empty();
    }
    return Optional.of(readStatusEntity);
  }
}
