package com.sprint.mission.discodeit.event;

import lombok.Getter;
import org.slf4j.MDC;

import java.util.UUID;

@Getter
public class BinaryContentSaveFailEvent {
    private final UUID binaryContentId;
    private final String requestId;
    private final String errorMessage;
    private final UUID senderId;

    public BinaryContentSaveFailEvent(UUID binaryContentId, String errorMessage, UUID senderId) {
        this.binaryContentId = binaryContentId;
        this.requestId = MDC.get("requestId");
        this.errorMessage = errorMessage;
        this.senderId = senderId;
    }
}
