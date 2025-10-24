package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;

import java.util.UUID;

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public abstract class UserMapper {
    @Autowired
    SessionRegistry sessionRegistry;

    @Mapping(target = "online", expression = "java(isOnline(user.getId()))")
    public abstract UserDto toDto(User user);

    public boolean isOnline(UUID userId) {
        var principals = sessionRegistry.getAllPrincipals();
        for (Object principal : principals) {
            if (principal instanceof DiscodeitUserDetails) {
                DiscodeitUserDetails discodeitUserDetails = (DiscodeitUserDetails) principal;
                if (discodeitUserDetails.getUserDto().id().equals(userId)) {
                    return true;
                }
            }
        }
        return false;
    }
}


