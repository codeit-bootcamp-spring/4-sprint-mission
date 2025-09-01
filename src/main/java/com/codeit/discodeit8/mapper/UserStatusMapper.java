package com.codeit.discodeit8.mapper;

import com.codeit.discodeit8.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

  @Mapping(target = "userId", source = "user.id")
  UserStatusDto toUserStatusDto(UserStatus userStatus);
}
