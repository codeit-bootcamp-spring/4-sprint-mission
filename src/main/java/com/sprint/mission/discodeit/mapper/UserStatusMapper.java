package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

  default UserStatus mapToEntity(User user, Instant lastActiveAt) {
    return new UserStatus(user, lastActiveAt);
  }

  @Mapping(source = "newLastActiveAt", target = "lastActiveAt")
  UserStatus mapToUpdateEntity(UserStatusUpdateRequest request);

  UserStatusDto mapToDto(UserStatus userStatus);

  List<UserStatusDto> mapToDtoList(List<UserStatus> userStatuses);
}
