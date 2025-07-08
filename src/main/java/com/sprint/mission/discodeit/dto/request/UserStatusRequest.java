package com.sprint.mission.discodeit.dto.request;

import java.time.Instant;
import java.util.UUID;

public class UserStatusRequest {
    public static class Create {
        UUID userId;
        Instant lastActivateAt;
    }
    public static class Update {
        Instant newLastActivateAt;
    }
}
