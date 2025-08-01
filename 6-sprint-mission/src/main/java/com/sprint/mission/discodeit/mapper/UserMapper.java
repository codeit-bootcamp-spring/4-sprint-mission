package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = BinaryContentMapper.class)
public interface UserMapper {

    @Mapping(target = "status", ignore = true)
    User userCreateServiceRequestToUser(UserCreateServiceRequest serviceRequest);

    @Named("userToUserDto")
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "profile", source = "binaryContentDto")
    @Mapping(target = "online", expression = "java(user.getStatus() != null && user.getStatus().isOnline())")
    UserDto userToUserDto(User user, BinaryContentDto binaryContentDto);
}
