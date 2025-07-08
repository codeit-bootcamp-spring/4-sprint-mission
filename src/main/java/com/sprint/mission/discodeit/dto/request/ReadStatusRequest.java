package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public class ReadStatusRequest {
    public static class Create {
        UUID userId;
        UUID channelId;
        Instant lastReadAt;
    }

    public static class Update {
        Instant newLastReadAt;
    }
}
