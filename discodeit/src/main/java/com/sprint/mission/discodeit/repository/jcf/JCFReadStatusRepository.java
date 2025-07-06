package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private static final JCFReadStatusRepository instance = new JCFReadStatusRepository();
    private final Map<UUID, Map<UUID, ReadStatus>> data;

    private JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        data.computeIfAbsent(readStatus.getChannelId(), k -> new HashMap<>())
                .put(readStatus.getUserId(), readStatus);

        return readStatus;
    }

    public Optional<ReadStatus> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        Map<UUID, ReadStatus> userMap = data.get(channelId);
        if (userMap == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(userMap.get(userId));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .map(channelMap -> channelMap.get(userId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return new ArrayList<>(
                Optional.ofNullable(data.get(channelId))
                        .map(Map::values)
                        .orElse(Collections.emptyList())
        );

    }

    @Override
    public boolean existsById(UUID userId, UUID channelId) {
        return data.containsKey(userId);
    }

    @Override
    public void deleteByChannelIdAndUserId(UUID channelId, UUID userId) {
        Optional.ofNullable(data.get(channelId)).ifPresent(map -> {
            map.remove(userId);
            if (map.isEmpty()) data.remove(channelId);
        });
    }

    @Override
    public void deleteByUserId(UUID userId) {
        for (Map<UUID, ReadStatus> userMap : data.values()) {
            userMap.remove(userId);
        }
        // 비어 있는 채널 제거
        data.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.remove(channelId);
    }
}

