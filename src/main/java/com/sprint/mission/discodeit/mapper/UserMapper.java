package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class, UserStatusMapper.class})
public interface UserMapper {

  User mapToEntity(UserCreateRequest request);

  @Mapping(source = "newUsername", target = "username")
  @Mapping(source = "newEmail", target = "email")
  @Mapping(source = "newPassword", target = "password")
  User mapToUpdatedEntity(UserUpdateRequest request);

  @Mapping(target = "online", expression = "java(user.getStatus() != null && user.getStatus().isOnline())")
  UserDto mapToDto(User user);

  List<UserDto> mapToDtoList(List<User> users);
}
