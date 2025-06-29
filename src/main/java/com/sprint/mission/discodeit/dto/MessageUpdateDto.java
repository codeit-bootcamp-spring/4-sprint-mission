package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageUpdateDto {
    UUID messageId;
    String newContent;
    boolean hasFile;
    List<byte[]> files;
}
