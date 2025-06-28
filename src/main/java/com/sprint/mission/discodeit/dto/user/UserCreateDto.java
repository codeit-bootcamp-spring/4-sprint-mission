package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;

public record UserCreateDto(
   String username,
   String email,
   String password,
   BinaryContentCreateDto binaryContentCreateDto
) {}
