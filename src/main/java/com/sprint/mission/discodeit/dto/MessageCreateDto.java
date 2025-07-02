package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageCreateDto {

    private String content;
    private UUID channelId;
    private UUID authorId;
    private List<UUID> attachmentIds;
}
