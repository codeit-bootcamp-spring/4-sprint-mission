package com.sprint.mission.discodeit.dto.channel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllChannelByUserIdResponseDto {
    List<ChannelResponseDto> publicChannels;
    List<ChannelResponseDto> privateChannels;
}
