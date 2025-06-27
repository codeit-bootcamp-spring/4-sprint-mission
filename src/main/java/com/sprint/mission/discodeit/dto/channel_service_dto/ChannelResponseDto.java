package com.sprint.mission.discodeit.dto.channel_service_dto;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import lombok.Getter;

import java.time.Instant;
import java.util.*;

@Getter
public class ChannelResponseDto {
    private final UUID channelId;
    private final String channelName;
    private final UUID hostUserId;
    private final ChannelType channelType;
    private final String channelDescription;

    private final UUID lastMessageId; // 추가된 필드

    private final Set<UUID> userIds;
    private final Set<UUID> messageIds;

    public ChannelResponseDto(Channel channel, UUID lastMessageId) {
        this.channelId = channel.getId();
        this.channelName = channel.getChannelName();
        this.hostUserId = channel.getHostUserId();
        this.channelType = channel.getChannelType();
        this.channelDescription = channel.getChannelDescription();
        this.userIds = channel.getUserIds();
        this.messageIds = channel.getMessageIds();
        this.lastMessageId = lastMessageId;
    }
}
