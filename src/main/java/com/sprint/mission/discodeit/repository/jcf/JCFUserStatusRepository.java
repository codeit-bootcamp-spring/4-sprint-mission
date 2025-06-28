package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFUserStatusRepository implements UserStatusRepository {

    // Key: id, Value: userStatus
    private final Map<UUID, UserStatus> data;

    public JCFUserStatusRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public UserStatus save(UserStatus userStatus) {
        data.put(userStatus.getId(), userStatus);
        return userStatus;
    }

    @Override
    public Optional<UserStatus> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public Optional<UserStatus> findByUserId(UUID userId) {
       return data.values().stream()
               .filter(userStatus -> userStatus.getUserId().equals(userId))
               .findFirst();
    }

    @Override
    public List<UserStatus> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        if (data.get(id) == null) {
            System.out.println(this.getClass().getSimpleName() + ".deleteByUserId() 실패: 이미 삭제되었습니다.");
            return;
        }
        data.remove(id);
    }
}
