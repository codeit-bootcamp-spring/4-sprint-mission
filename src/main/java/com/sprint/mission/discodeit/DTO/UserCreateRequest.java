package com.sprint.mission.discodeit.DTO;

import com.sprint.mission.discodeit.entity.User;

public record UserCreateRequest(

        String username,
        String email,
        String password,
        byte[] profileImageData
) { }
