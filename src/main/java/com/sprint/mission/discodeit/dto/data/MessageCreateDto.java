package com.sprint.mission.discodeit.dto.data;
import java.util.UUID;
public record MessageCreateDto( // record
                                String content,
                                UUID channelId,
                                UUID userId
) {}
