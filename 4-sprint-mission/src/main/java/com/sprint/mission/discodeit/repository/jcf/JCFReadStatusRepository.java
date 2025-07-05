package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data = new ConcurrentHashMap<>();

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        ReadStatus readStatus = data.get(id);
        if (readStatus != null) {
            data.remove(id);
        }
    }

    @Override
    public List<ReadStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        ReadStatus readStatus = data.get(id);
        if (readStatus == null) {
            return Optional.empty();
        }
        return Optional.of(readStatus);
    }
}
