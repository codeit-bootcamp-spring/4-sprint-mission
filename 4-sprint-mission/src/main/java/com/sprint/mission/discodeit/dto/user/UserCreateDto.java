package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;

public record UserCreateDto(
        String userName,
        String userEmail,
        String password,
        boolean status,
        BinaryContentRequestDto binaryContentDto) {}
