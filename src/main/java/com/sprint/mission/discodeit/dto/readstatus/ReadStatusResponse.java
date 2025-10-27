package com.sprint.mission.discodeit.dto.readstatus;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ReadStatusResponse {

    private final UUID id;
    private final UUID channelId;
    private final UUID userId;
    private final Instant lastReadAt;

    public ReadStatusResponse (ReadStatus readStatus) {
        this.id = readStatus.getId();
        this.channelId = readStatus.getChannelId();
        this.userId = readStatus.getUserId();
        this.lastReadAt = readStatus.getLastReadAt();
    }

}
