package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record UserUpdateEvent(
        UserDto userDto,
        String eventType
) {
}
