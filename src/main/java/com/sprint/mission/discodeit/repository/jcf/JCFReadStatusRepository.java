package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFReadStatusRepository implements ReadStatusRepository {
    private final List<ReadStatus> data = new ArrayList<>();

    @Override
    public List<ReadStatus> findAll() {
        return data;
    }

    @Override
    public void save(ReadStatus readStatus) {
        if (data.contains(readStatus)) {
            data.stream()
                    .map(rs -> rs.equals(readStatus) ? readStatus : rs)
                    .forEach(rs -> {
                    });
        } else {
            data.add(readStatus);
        }
    }

    @Override
    public ReadStatus findById(UUID id) {
        ReadStatus readStatus = data.stream()
                .filter(rs -> rs.getId().equals(id))
                .findFirst().orElseThrow(() -> new BusinessLogicException(ExceptionCode.READSTATUS_NOT_FOUND));
        return readStatus;
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(rs -> rs.getId().equals(id));
    }

    @Override
    public ReadStatus findByChannelIdAndUserId(UUID channelId, UUID userId) {
        ReadStatus findReadStatus = findAll().stream()
                .filter(readStatus -> readStatus.getChannelId().equals(channelId) && readStatus.getUserId().equals(userId))
                .findFirst().orElseThrow(() -> new BusinessLogicException(ExceptionCode.READSTATUS_NOT_FOUND));
        return findReadStatus;
    }

    @Override
    public List<UUID> findByChannelId(UUID channelId) {
        List<UUID> ids = new ArrayList<>();
        data.stream()
                .filter(rs -> rs.getChannelId().equals(channelId))
                .forEach(rs -> ids.add(rs.getId()));
        return ids;
    }
}
