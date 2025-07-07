package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class UserMapper {
    /**
     * UserCreateDto → User 엔티티 변환 (id, timestamps 제외)
     */
    public User toEntity(UserCreateDto dto) {
        return new User(
                dto.getUsername(),
                dto.getEmail(),
                dto.getPassword(),
                null
        );
    }

    /**
     * UserUpdateDto → 기존 User 엔티티 덮어쓰기
     */
    public void updateEntity(UserUpdateDto dto, User user) {
        if (dto.getUsername() != null) user.updateUsername(dto.getUsername());
        if (dto.getEmail()    != null) user.updateEmail(dto.getEmail());
        if (dto.getPassword() != null) user.updatePassword(dto.getPassword());
        user.touch();
    }


    /**
     * User + online flag → UserResponseDto 변환
     */
    public UserResponseDto toDto(User user, boolean online) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getProfileId(),
                online,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * User → UserResponseDto 리스트로 변환
     */
    public List<UserResponseDto> toDtoListWithStatus(List<User> users, Map<UUID, UserStatus> statusMap) {
        return users.stream()
                .map(user -> {
                    UserStatus status = statusMap.get(user.getId());
                    boolean online = status != null && status.isOnline();
                    return toDto(user, online);
                })
                .toList();
    }

}