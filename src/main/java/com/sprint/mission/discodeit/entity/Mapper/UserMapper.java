package com.sprint.mission.discodeit.entity.Mapper;

import com.sprint.mission.discodeit.entity.DTO.CreateUserDTO;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public CreateUserDTO userCreateDtoToUser(User user) {
        String nickName = user.getNickName();
        String email = user.getEmail();
        return new CreateUserDTO(nickName, email);
    }

}