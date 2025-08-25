package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.user_status_dto.UserStatusDto;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-14T14:47:04+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class UserStatusMapperImpl implements UserStatusMapper {

    @Override
    public UserStatusDto toUserStatusDto(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }

        UUID userId = null;
        UUID id = null;
        Instant lastActiveAt = null;

        userId = userStatusUserId( userStatus );
        id = userStatus.getId();
        lastActiveAt = userStatus.getLastActiveAt();

        UserStatusDto userStatusDto = new UserStatusDto( id, userId, lastActiveAt );

        return userStatusDto;
    }

    private UUID userStatusUserId(UserStatus userStatus) {
        if ( userStatus == null ) {
            return null;
        }
        User user = userStatus.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
