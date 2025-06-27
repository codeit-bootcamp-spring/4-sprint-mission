package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class MessageCreateDto {
    private UUID authorId;
    private UUID channelId;
    private String content;
    private List<BinaryContentDto> attachments;
}
