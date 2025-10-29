package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BinaryContentUploadFailureEvent {
    private String requestId;
    private UUID binaryContentId;
    private String reason;
}
