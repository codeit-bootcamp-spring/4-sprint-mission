package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateDto {
    String content;
    UUID channelId;
    UUID authorId;
    List<UUID> attachmentId;
}
