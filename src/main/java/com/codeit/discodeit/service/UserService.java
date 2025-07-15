package com.codeit.discodeit.service;

import com.codeit.discodeit.dto.user_service_dto.*;
import com.codeit.discodeit.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

  User updateUser(UserUpdateRequest userUpdateRequest, MultipartFile profileImage)
      throws IOException;

  void deleteUser(UUID userId);

  User createUser(UserCreateRequest userCreateRequest) throws IOException;

  List<UserDto> findAllUserDto();

  User findUserByUserId(UUID userId);

}
