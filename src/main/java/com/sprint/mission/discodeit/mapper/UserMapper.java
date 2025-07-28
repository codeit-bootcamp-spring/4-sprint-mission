package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "userStatus", ignore = true)
    @Mapping(target = "profile", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(source = "profile", target = "profile")
    @Mapping(target = "online", ignore = true)
    UserDto toDto(User user);
}
