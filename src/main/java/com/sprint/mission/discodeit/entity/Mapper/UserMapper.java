package com.sprint.mission.discodeit.entity.Mapper;

import com.sprint.mission.discodeit.dto.data.CreateUserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public CreateUserDto userCreateDtoToUser(User user) {
        String nickName = user.getNickName();
        String email = user.getEmail();
        return new CreateUserDto(nickName, email);
    }
}