package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix="discodeit.repository",
        name="type",
        havingValue="jcf",
        matchIfMissing=true
)
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final Map<UUID, ReadStatus> data;

    public JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        this.data.put(readStatus.getId(), readStatus);
        return readStatus;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<ReadStatus> findByUserId(UUID userId) {
        return this.data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<ReadStatus> findByChannelId(UUID channelId) {
        return this.data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .findFirst();
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return this.data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return this.data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return this.data.values().stream()
                .anyMatch(rs -> rs.getUserId().equals(userId));
    }

    @Override
    public boolean existsByUserIdAndChannelId(UUID userId, UUID channelId) {
        return this.data.values().stream()
                .anyMatch(rs -> rs.getUserId().equals(userId)
                        && rs.getChannelId().equals(channelId));
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        this.data.values()
                .removeIf(rs -> rs.getChannelId().equals(channelId));
    }
}
