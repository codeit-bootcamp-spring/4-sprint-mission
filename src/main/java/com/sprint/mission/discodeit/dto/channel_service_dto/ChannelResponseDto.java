package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ChannelResponseDto {
    private UUID channelId;
    private String channelName;
    private UUID hostUserId;
    private ChannelType channelType;
    private String channelDescription;
    private Set<UUID> userIds;
    private Set<UUID> messageIds;

    public ChannelResponseDto(Channel channel) {
        this.channelId = channel.getId();
        this.channelName = channel.getChannelName();
        this.hostUserId = channel.getHostUserId();
        this.channelType = channel.getChannelType();
        this.channelDescription = channel.getChannelDescription();
        this.userIds = channel.getUserIds();
        this.messageIds = channel.getMessageIds();
    }
}
