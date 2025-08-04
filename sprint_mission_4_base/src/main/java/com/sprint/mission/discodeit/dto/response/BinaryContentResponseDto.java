package com.sprint.mission.discodeit.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BinaryContentResponseDto {
    private UUID id;
    private Instant createdAt;
    //
    private String fileName;
    private Long size;
    private String contentType;
    private byte[] bytes;
}
