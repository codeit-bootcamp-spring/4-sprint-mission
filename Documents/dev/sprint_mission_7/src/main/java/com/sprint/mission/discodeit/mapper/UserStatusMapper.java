package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserStatusMapper {

    UserStatus userStatusCreateDtoToUserStatus (UserStatusCreateDto dto);
    UserStatusDto userStatusToUserStatusResponseDto(UserStatus userStatus);

//    public UserStatus userStatusCreateDtoToUserStatus(UserStatusCreateDto dto) {
//        return new UserStatus(
//                dto.getUserId()
//        );
//    }
//    public UserStatusResponseDto userStatusToUserStatusResponseDto(UserStatus userStatus) {
//        return new UserStatusResponseDto(
//                userStatus.getId(),
//                userStatus.getCreatedAt(),
//                userStatus.getUpdatedAt(),
//                userStatus.getUserId(),
//                userStatus.getLastAccessedAt(),
//                userStatus.isOnline()
//        );
//    }
}
