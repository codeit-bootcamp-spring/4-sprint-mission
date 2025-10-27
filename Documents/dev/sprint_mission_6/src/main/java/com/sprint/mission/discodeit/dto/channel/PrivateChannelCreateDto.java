package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PrivateChannelCreateDto {
    private ChannelType Type;
    private String name;
    private List<UUID> participants;
}
