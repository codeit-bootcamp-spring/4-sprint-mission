package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@ToString
public class BinaryContentResponseDto {
    private final UUID id;
    private final byte[] bytes;
    private final BinaryContentType type;
    private final Instant createdAt;
}
