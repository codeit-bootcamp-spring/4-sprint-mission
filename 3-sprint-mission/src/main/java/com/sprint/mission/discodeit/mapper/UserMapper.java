package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserMapper {
    private final BinaryContentRepository binaryContentRepository;

    public User userCreateDtotoUser(UserCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserCreateDto는 null일 수 없습니다.");
        }
        return new User(dto.userName(), dto.userEmail(), dto.password());
    }

    public UserResponseDto userToUserResponseDto(User user, boolean status, byte[] image) {
        return new UserResponseDto(
                user.getId(), user.getUserName(), user.getUserEmail(), status, image);
    }
}
