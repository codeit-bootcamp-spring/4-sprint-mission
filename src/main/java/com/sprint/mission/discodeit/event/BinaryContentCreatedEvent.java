package com.sprint.mission.discodeit.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class BinaryContentCreatedEvent {
    private final UUID binaryContentId;
    private final byte[] bytes;
}
