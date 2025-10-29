package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BinaryContentCreatedEvent {
    private UUID binaryContentId;
    private byte[] bytes;
}
