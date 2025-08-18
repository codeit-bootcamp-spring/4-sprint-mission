package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userCreateDtoToUser(UserCreateRequest dto, UUID fileId);
    UserDto userToUserDto(User user);

//    public User userCreateDtoToUser(UserCreateRequest dto, UUID fileId) {
//        User user = new User(
//                dto.getUsername(),
//                dto.getEmail(),
//                dto.getPassword(),
//                fileId,
//                dto.getStatus()
//                //Userstatus 생성되도록해주기!
//                );
//        return user;
//    }

//    public UserResponseDto userToUserResponseDto(User user) {
//        return new UserResponseDto(
//                user.getId(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getPassword(),
//                user.getFileId(),
//                user.getUserStatus() != null && user.getUserStatus().isOnline(),
//                user.getUserStatus(),
//                user.getCreatedAt(),
//                user.getUpdatedAt()
//        );
//    }
//    public UserDto userToUserDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getCreatedAt(),
//                user.getUpdatedAt(),
//                user.getUsername(),
//                user.getEmail(),
//                user.getFileId(),
//                user.getUserStatus() != null && user.getUserStatus().isOnline()
//        );
//    }
}
