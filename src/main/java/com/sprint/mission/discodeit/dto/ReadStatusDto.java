package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.validator.ValidUUID;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusDto {

    @Getter
    @AllArgsConstructor
    public static class create {
        @ValidUUID
        private UUID userId;
        @ValidUUID
        private UUID channelId;

        public ReadStatus toReadStatus(Instant lastReadAt) {
            return new ReadStatus(
                    userId,
                    channelId,
                    lastReadAt
            );
        }
    }

    public record response(
            UUID id,
            UUID userId,
            UUID channelId,
            Instant lastReadAt
    ) {
    }
}
