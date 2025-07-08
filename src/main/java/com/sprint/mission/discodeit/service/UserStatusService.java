package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserStatusRequest;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserStatusService {
    UserDto createUserStatus(UserStatusRequest userStatusRequest);
    UserDto findUserStatus(UUID userStatusId); // userStatus를 찾기 위해 userStatus의 UID를 주입
    List<UserStatus> findAllUserStatus();
   UserDto updateUserStatus(UUID userId, UserStatusRequest userStatusRequest);
    // UserId를 넣은 이유는 user의 상태를 수정하는거니까.. 같다
    void deleteUserStatus(UUID userStatusId);

}
