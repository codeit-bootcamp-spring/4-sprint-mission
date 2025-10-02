package com.sprint.mission.discodeit.dto.binarycontent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Schema(name = "BinaryContentDto")
@Getter
@AllArgsConstructor
public class BinaryContentDto {
        private UUID id;
        private String fileName;
        private Long size;
        private String contentType;
        private byte[] bytes;

}
