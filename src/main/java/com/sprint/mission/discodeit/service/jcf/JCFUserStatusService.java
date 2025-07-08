package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JCFUserStatusService implements UserStatusService {
    @Override
    public UserStatus createUserStatus(UserStatusRequest userStatusRequest) {
        return null;
    }

    @Override
    public UserStatus findUserStatus(UUID userStatusId) {
        return null;
    }

    @Override
    public List<UserStatus> findAllUserStatus() {
        return List.of();
    }

    @Override
    public UserDto updateUserStatus(UUID userId, UserStatusRequest userStatusRequest) {
        return null;
    }

    @Override
    public void deleteUserStatus(UUID userStatusId) {

    }
}
