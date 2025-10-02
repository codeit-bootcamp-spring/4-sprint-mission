package com.sprint.mission.discodeit.dto.channel;


import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChannelResponse {
    private final UUID id;
    private final ChannelType type;
    private String name;
    private String description;
    private final List<UserResponse> participants;
    private final Instant lastMessageAt;
}
