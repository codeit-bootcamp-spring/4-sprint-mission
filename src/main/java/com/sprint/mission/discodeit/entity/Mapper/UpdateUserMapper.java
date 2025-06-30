package com.sprint.mission.discodeit.entity.Mapper;

import com.sprint.mission.discodeit.entity.DTO.UpdateUserDTO;
import com.sprint.mission.discodeit.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UpdateUserMapper {
    public UpdateUserDTO updateUserDTO(User user) {
        String nickName = user.getNickName();
        String email = user.getEmail();
        return new UpdateUserDTO(nickName, email);
    }
}
