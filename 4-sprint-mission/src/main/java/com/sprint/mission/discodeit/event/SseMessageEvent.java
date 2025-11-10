package com.sprint.mission.discodeit.event;

public record SseMessageEvent(
        String name,
        Object data
) {
}
