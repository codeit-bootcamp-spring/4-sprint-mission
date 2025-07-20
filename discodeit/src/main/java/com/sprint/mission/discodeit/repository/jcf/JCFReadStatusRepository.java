package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class JCFReadStatusRepository implements ReadStatusRepository {
    private static final JCFReadStatusRepository instance = new JCFReadStatusRepository();
    private final Map<UUID, ReadStatus> data;

    private JCFReadStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public ReadStatus save(ReadStatus readStatus) {
        ReadStatus toSave;

        if (readStatus.getId() == null) {
            // ID가 없으면 새로 생성
            toSave = new ReadStatus(
                    UUID.randomUUID(),
                    readStatus.getUserId(),
                    readStatus.getChannelId(),
                    Instant.now()  // 생성 시점 시간
            );
        } else {
            // 기존 ReadStatus 사용
            toSave = readStatus;
        }

        // 마지막 읽은 시간 갱신
        toSave.updateReadTime(Instant.now());

        // Map에 저장
        data.put(toSave.getId(), toSave);

        return toSave;
    }

    @Override
    public Optional<ReadStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<ReadStatus> findAllByUserId(UUID userId) {
        return data.values().stream()
                .filter(rs -> rs.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReadStatus> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(UUID readStatusId) {
        return data.values().stream()
                .anyMatch(rs -> rs.getUserId().equals(readStatusId));
    }

    @Override
    public void deleteById(UUID readStatusId) {
        data.entrySet().removeIf(entry ->
                entry.getValue().getChannelId().equals(readStatusId));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.entrySet().removeIf(entry ->
                entry.getValue().getUserId().equals(userId));
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.entrySet().removeIf(entry ->
                entry.getValue().getChannelId().equals(channelId));
    }
}

