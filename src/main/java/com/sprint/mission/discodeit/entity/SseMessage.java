package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SseMessage {
    private UUID id;
    private String eventName;
    private Object data;
}
