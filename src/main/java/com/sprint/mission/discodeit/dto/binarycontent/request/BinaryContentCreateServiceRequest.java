package com.sprint.mission.discodeit.dto.binarycontent.request;

import com.sprint.mission.discodeit.entity.BinaryContent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BinaryContentCreateServiceRequest {

    String fileName;
    String contentType;
    byte[] bytes;

    public BinaryContent toEntity() {
        return BinaryContent.builder()
                .fileName(fileName)
                .contentType(contentType)
                .bytes(bytes)
                .build();
    }
}
