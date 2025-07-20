package com.sprint.mission.discodeit.mapper;


import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.sprint.mission.discodeit.dto.UserDto.*;

import java.util.Optional;


@Component
@AllArgsConstructor
public class UserMapper {

    private final UserStatusMapper userStatusMapper;

    // 1. UserCreateRequest → User (Entity)
    public User toEntity(UserCreateRequest dto) {
        return new User(dto.username(), dto.email(), dto.password());
    }

    // 2. User → UserResponse (간단 응답)
    public UserResponse toUserResponse(User user, Optional<UserStatus> optionalUserStatus) {
            UserStatus userStatus = optionalUserStatus.orElse(null);

            return new UserResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileId(),
                    userStatus == null ? null : userStatusMapper.toUserStatusResponse(userStatus)
            );
        }

    // 3. User → UserResponseDto (상세 응답)
    public UserResponseDto toUserResponse(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getProfileId()
        );
    }

    // 4. User + UserStatus → AllUserResponesDto (전체 조회용)
    public AllUserResponseDto toAllUserResponesDto(User user, UserStatus status) {

        boolean isOnline = status != null && status.isOnline();

        return new AllUserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                isOnline
        );
    }

    // 5. User → UserUpdateResponse
    public UserUpdateResponse toUserUpdateResponse(User user) {
        return new UserUpdateResponse(
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getPassword()
        );
    }
}