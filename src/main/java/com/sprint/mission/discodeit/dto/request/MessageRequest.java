package com.sprint.mission.discodeit.dto.request;

import java.util.UUID;

public class MessageRequest {
    public static class Create {
        UUID channelId;
        UUID userId;
        String content;
    }

    public static class Update {
        String newContent;
    }
}
