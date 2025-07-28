package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    UserStatusDto toDto(UserStatus userStatus);

    UserStatus toEntity(UserStatusCreateRequest createRequest);

    @Mapping(target = "lastActiveAt", source = "newLastActiveAt")
    void updateFromRequest(UserStatusUpdateRequest request, @MappingTarget UserStatus userStatus);
}
