package com.sprint.mission.discodeit.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageCreatedEvent {
    private UUID id;
    private UUID authorId;
    private UUID channelId;
    private String content;
}
