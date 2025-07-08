package com.sprint.mission.discodeit.entity.Mapper;

import com.sprint.mission.discodeit.dto.data.UpdateUserDto;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserMapper {
    public UpdateUserDto updateUserDTO(User user) {
        String nickName = user.getNickName();
        String email = user.getEmail();
        return new UpdateUserDto(nickName, email);
    }
}
