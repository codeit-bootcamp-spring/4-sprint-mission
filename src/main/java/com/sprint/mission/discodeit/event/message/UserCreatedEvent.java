package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.UserDto;

import java.time.Instant;

public class UserCreatedEvent extends CreatedEvent<UserDto> {
    public UserCreatedEvent(UserDto data, Instant createdAt) { super(data, createdAt); }
    public static UserCreatedEvent now(UserDto data) { return new UserCreatedEvent(data, Instant.now()); }
}
