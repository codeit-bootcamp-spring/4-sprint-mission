package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto.*;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponseDto create(UserCreateRequest request, @Nullable MultipartFile profile);
//    UserDto findById(UUID userId);
    List<AllUserResponseDto> findAll();
    UserUpdateResponse update(UUID userId, UserUpdateRequest updateDto, @Nullable MultipartFile profile);
    void delete(UUID userId);
}
