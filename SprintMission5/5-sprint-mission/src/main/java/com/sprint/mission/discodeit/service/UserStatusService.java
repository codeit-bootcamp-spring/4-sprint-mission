package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import java.util.List;
import java.util.UUID;

public interface UserStatusService {

  public UserStatusResponseDto createUserStatus(UUID userId);

  public UserStatusResponseDto findUserStatusById(UUID id);

  public List<UserStatusResponseDto> findAllUserStatus();

  public UserStatusResponseDto updateUserStatus(UserStatusUpdateDto userStatusUpdateDto);

  public UserStatusResponseDto updateUserStatusByUserId(UUID userId,
      UserStatusUpdateDto userStatusUpdateDto);

  public void deleteUserStatusById(UUID id);
}
