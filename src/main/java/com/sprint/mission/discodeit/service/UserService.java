package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto.*;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  UserGetDto create(UserCreateRequest userCreateRequest, MultipartFile profile);

  UserResponse find(UUID userId);

  List<AllUserGetDto> findAll(); // 심화 요구사항

  UserUpdateResponse update(UUID userId, UserUpdateRequest userUpdateRequest,
      MultipartFile profile);

  void delete(UUID id);

  void deleteAll();
}
