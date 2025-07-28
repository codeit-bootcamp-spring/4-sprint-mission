package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { BinaryContentMapper.class, UserStatusMapper.class })
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "profile", source = "profile")
    User toEntity(UserCreateRequest request, BinaryContentCreateRequest profile);

    @Mapping(target = "profile", ignore = true)
    void updateFromDto(UserUpdateRequest request, @MappingTarget User user);

}