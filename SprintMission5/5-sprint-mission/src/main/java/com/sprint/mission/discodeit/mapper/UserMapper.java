package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {

  public UserEntity toUser(UserCreateServiceRequest serviceRequest) {
    if (serviceRequest == null) {
      throw new IllegalArgumentException(
          "UserCreateServiceRequest는 null일 수 없습니다.");
    }
    return new UserEntity(
        serviceRequest.username(),
        serviceRequest.email(),
        serviceRequest.password()
    );
  }

  public void updateFromUserUpdateServiceRequest(UserEntity userEntity,
      UserUpdateServiceRequest serviceRequest) {
    if (userEntity == null || serviceRequest == null) {
      throw new IllegalArgumentException("엔티티 또는 UserUpdateServiceRequest는 null일 수 없습니다.");
    }
    if (serviceRequest.newUsername() != null) {
      userEntity.setUsername(serviceRequest.newUsername());
    }
    if (serviceRequest.newEmail() != null) {
      userEntity.setEmail(serviceRequest.newEmail());
    }
    if (serviceRequest.newPassword() != null) {
      userEntity.setPassword(serviceRequest.newPassword());
    }
  }

  public UserDto toUserDto(UserEntity userEntity) {
    if (userEntity == null) {
      return null;
    }
    return new UserDto(
        userEntity.getId(),
        userEntity.getCreatedAt(),
        userEntity.getUpdatedAt(),
        userEntity.getUsername(),
        userEntity.getEmail(),
        userEntity.getProfileId(),
        userEntity.isOnline()
    );
  }
}
