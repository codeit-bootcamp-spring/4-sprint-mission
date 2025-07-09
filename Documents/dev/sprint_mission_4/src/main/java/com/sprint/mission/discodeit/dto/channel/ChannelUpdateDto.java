package com.sprint.mission.discodeit.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
@Getter
@AllArgsConstructor
public class ChannelUpdateDto {
    UUID channelId;
    String newName;
    String newDescription;
}
