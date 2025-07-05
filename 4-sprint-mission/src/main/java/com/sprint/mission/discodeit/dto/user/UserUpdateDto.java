package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import java.util.UUID;

public record UserUpdateDto(UUID id, String name, BinaryContentRequestDto image) {}
