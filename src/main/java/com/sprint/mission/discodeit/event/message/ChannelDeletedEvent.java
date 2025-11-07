package com.sprint.mission.discodeit.event.message;

import com.sprint.mission.discodeit.dto.data.ChannelDto;

import java.time.Instant;

public class ChannelDeletedEvent extends DeletedEvent<ChannelDto> {
    public ChannelDeletedEvent(ChannelDto data, Instant deletedAt){ super(data, deletedAt); }
    public static ChannelDeletedEvent now(ChannelDto data){ return new ChannelDeletedEvent(data, Instant.now()); }
}
