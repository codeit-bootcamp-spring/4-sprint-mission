package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {
    private final List<UserStatus> data = new ArrayList<>();

    @Override
    public void delete(UUID id) {
        data.removeIf(userStatus -> userStatus.getId().equals(id));
    }

    @Override
    public void deleteByUserId(UUID userId) {
        data.removeIf(userStatus -> userStatus.getUserId().equals(userId));
    }

    @Override
    public UserStatus findById(UUID id) {
        UserStatus userStatus = data.stream()
                .filter(us -> us.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USERSTATUS_NOT_FOUND));
        return userStatus;
    }

    @Override
    public UserStatus findByUserId(UUID userId) {
        UserStatus userStatus = data.stream()
                .filter(us -> us.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.USERSTATUS_NOT_FOUND));
        return userStatus;
    }

    @Override
    public void save(UserStatus userStatus) {
        if (data.contains(userStatus)) {
            data.stream()
                    .map(us -> us.equals(userStatus.getId()) ? userStatus : us)
                    .forEach(m -> {
                    });
        } else {
            data.add(userStatus);
        }
    }

    @Override
    public List<UserStatus> findAll() {
        return data;
    }
}
