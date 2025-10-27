package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;
@Getter
@AllArgsConstructor
public class ChannelUpdateDto {
    UUID channelId;

    @Size(min = 2, max = 20, message = "채널명은 2~20자 사이여야 합니다.")
    String newName;

    @Size(min = 2, max = 100, message = "채널 소개는 2~100자 사이여야 합니다.")
    String newDescription;
}
