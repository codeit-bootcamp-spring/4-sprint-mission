package com.sprint.mission.discodeit.dto.readstatus_dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class ReadStatusResponseDto {
    private UUID readStatusId;
    private final UUID userId;
    private final UUID channelId;
    private Instant readTime;

    public ReadStatusResponseDto(ReadStatus readStatus) {
        this.readStatusId = readStatus.getId();
        this.userId = readStatus.getUserId();
        this.channelId = readStatus.getChannelId();
        this.readTime = readStatus.getUpdatedAt();
    }

}
