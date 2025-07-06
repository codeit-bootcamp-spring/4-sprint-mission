package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ReadStatusCreateDto {
    private UUID userId;
    private UUID channelId;
}
