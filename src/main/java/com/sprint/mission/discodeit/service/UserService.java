package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.DTO.CreateUserDTO;
import com.sprint.mission.discodeit.entity.DTO.UpdateUserDTO;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    public CreateUserDTO createUser(User user);
    public CreateUserDTO searchUser(UUID id);
    public List<CreateUserDTO> searchAll();
    public UpdateUserDTO updateUser(UUID id, String newNickname);
    public void deleteUser(UUID id);

}
