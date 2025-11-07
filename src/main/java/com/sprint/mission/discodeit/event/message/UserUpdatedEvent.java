package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.UserDto;

import java.time.Instant;

public class UserUpdatedEvent extends UpdatedEvent<UserDto> {
    public UserUpdatedEvent(UserDto from, UserDto to, Instant updatedAt) { super(from, to, updatedAt); }
    public static UserUpdatedEvent now(UserDto from, UserDto to) { return new UserUpdatedEvent(from, to, Instant.now()); }
}
