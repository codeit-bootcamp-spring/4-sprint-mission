package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

    public UserStatusDto createUserStatus(UUID userId);

    public UserStatusDto findUserStatusById(UUID id);

    public List<UserStatusDto> findAllUserStatus();

    public UserStatusDto updateUserStatus(UserStatusUpdateDto userStatusUpdateDto);

    public UserStatusDto updateUserStatusByUserId(
            UUID userId, UserStatusUpdateDto userStatusUpdateDto);

    public void deleteUserStatusById(UUID id);
}
