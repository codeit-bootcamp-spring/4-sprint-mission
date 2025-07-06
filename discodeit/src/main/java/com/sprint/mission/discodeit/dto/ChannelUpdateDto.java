package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ChannelUpdateDto {
    private UUID channelId;
    private String newName;
    private String newDescription;
}
