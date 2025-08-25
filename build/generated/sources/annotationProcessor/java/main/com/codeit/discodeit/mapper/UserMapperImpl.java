package com.codeit.discodeit.mapper;

import com.codeit.discodeit.dto.binary_contents_dto.BinaryContentDto;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.entity.User;
import java.io.IOException;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-08-14T14:47:04+0900",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.14.3.jar, environment: Java 17.0.15 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private BinaryContentMapper binaryContentMapper;

    @Override
    public UserDto toUserDto(User user) {
        if ( user == null ) {
            return null;
        }

        Boolean online = null;
        UUID id = null;
        String username = null;
        String email = null;
        BinaryContentDto profile = null;

        online = UserMapper.isUserOnline( user.getStatus() );
        id = user.getId();
        username = user.getUsername();
        email = user.getEmail();
        profile = binaryContentMapper.toBinaryContentDto( user.getProfile() );

        UserDto userDto = new UserDto( id, username, email, profile, online );

        return userDto;
    }

    @Override
    public User toUser(UserCreateRequest request) throws IOException {
        if ( request == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.profile( UserMapper.multipartFileToBinaryContent( request.getProfileImage() ) );
        user.username( request.getUsername() );
        user.email( request.getEmail() );
        user.password( request.getPassword() );

        return user.build();
    }
}
