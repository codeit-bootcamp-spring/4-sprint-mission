package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MessageCreateDto {
    String content;
    UUID channelId;
    UUID userId;
    boolean hasFile;
    List<byte[]> files;
}
