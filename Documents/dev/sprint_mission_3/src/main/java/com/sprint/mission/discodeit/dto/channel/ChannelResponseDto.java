package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponseDto {
       private UUID id;
       private ChannelType type;
       private String channelName;
       private String description;
       private Instant recentMessageTime;
       private List<UUID>members;
}
