package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.io.IOException;
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

    public UserResponseDto userToUserResponseDto(User user, boolean status) {
        return new UserResponseDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUserName(),
                user.getUserEmail(),
                user.getProfileId(),
                status);
    }

    public UserCreateDto UserMultipartDtoToUserCreateDto(UserMultipartDto dto) {
        BinaryContentRequestDto binaryContentDto = null;
        if (dto.image() != null && !dto.image().isEmpty()) {
            try {
                binaryContentDto =
                        new BinaryContentRequestDto(dto.image().getBytes(), dto.image().getContentType());
            } catch (IOException e) {
                binaryContentDto = null;
            }
        }
        return new UserCreateDto(
                dto.userName(), dto.userEmail(), dto.password(), dto.status(), binaryContentDto);
    }

    public UserUpdateDto UserMultipartUpdateDtoToUserUpdateDto(UserMultipartUpdateDto dto) {
        BinaryContentRequestDto binaryContentDto = null;
        if (dto.image() != null && !dto.image().isEmpty()) {
            try {
                binaryContentDto =
                        new BinaryContentRequestDto(dto.image().getBytes(), dto.image().getContentType());
            } catch (IOException e) {
                binaryContentDto = null;
            }
        }
        return new UserUpdateDto(dto.id(), dto.userName(), binaryContentDto);
    }
}
