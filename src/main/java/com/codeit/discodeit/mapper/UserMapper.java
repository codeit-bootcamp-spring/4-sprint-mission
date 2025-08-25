package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.BinaryContent;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {

  @Mapping(target = "online", source = "status", qualifiedByName = "isUserOnline")
  UserDto toUserDto(User user);

  @Mapping(target = "profile", source = "profileImage", qualifiedByName = "multipartFileToBinaryContent")
  @Mapping(target = "status", ignore = true)
  User toUser(UserCreateRequest request) throws IOException;

  @Named("isUserOnline")
  static Boolean isUserOnline(UserStatus status) {
    if (status == null || status.getLastActiveAt() == null) {
      return false;
    }
    Duration duration = Duration.between(status.getLastActiveAt(), Instant.now());
    return duration.toMinutes() < 5;
  }

  @Named("multipartFileToBinaryContent")
  static BinaryContent multipartFileToBinaryContent(MultipartFile file) throws IOException {
    return BinaryContentMapper.attachmentToBinaryContent(file);
  }
}