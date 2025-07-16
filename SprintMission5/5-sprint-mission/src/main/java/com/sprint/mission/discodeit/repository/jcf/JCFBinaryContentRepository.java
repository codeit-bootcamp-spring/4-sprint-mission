package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.BinaryContentEntity;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFBinaryContentRepository implements BinaryContentRepository {

  private final Map<UUID, BinaryContentEntity> data = new ConcurrentHashMap<>();

  @Override
  public BinaryContentEntity save(BinaryContentEntity binaryContentEntity) {
    data.put(binaryContentEntity.getId(), binaryContentEntity);
    return binaryContentEntity;
  }

  @Override
  public Optional<BinaryContentEntity> findById(UUID id) {
    BinaryContentEntity binaryContentEntity = data.get(id);
    if (binaryContentEntity == null) {
      return Optional.empty();
    }
    return Optional.of(binaryContentEntity);
  }

  @Override
  public List<BinaryContentEntity> findAll() {
    return new ArrayList<>(data.values());
  }

  @Override
  public void delete(UUID id) {
    BinaryContentEntity binaryContentEntity = data.remove(id);
    if (binaryContentEntity != null) {
      data.remove(id);
    }
  }
}
