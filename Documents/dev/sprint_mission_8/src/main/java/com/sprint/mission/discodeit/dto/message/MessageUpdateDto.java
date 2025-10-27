package com.sprint.mission.discodeit.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MessageUpdateDto {

    private UUID messageId;
    private String newContent;
}
