package com.codeit.discodeit8.service;

import com.codeit.discodeit8.dto.user_service_dto.*;
import com.codeit.discodeit8.entity.BinaryContent;
import com.codeit.discodeit8.entity.User;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

  UserDto updateUser(UserUpdateRequest userUpdateRequest, BinaryContent profileImage,
      byte[] profileImgBytes)
      throws IOException;

  void deleteUser(UUID userId);

  UserDto createUser(UserCreateRequest userCreateRequest) throws IOException;

  List<UserDto> findAllUser();

  UserDto findUserDtoByUserId(UUID userId);

  User findUserByUserId(UUID userId);
}
