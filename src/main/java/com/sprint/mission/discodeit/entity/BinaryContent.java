package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.baseentity.ImmutableBaseEntity;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;

@Getter
public class BinaryContent extends ImmutableBaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    //
    private final String fileName;
    private final Long size;
    private final String contentType;
    private final byte[] bytes;

    public BinaryContent(String fileName, Long size, String contentType, byte[] bytes) {
        super();
        //
        this.fileName = fileName;
        this.size = size;
        this.contentType = contentType;
        this.bytes = bytes;
    }

    public BinaryContentDto.response toDto() {
        return new BinaryContentDto.response(
                super.getId(),
                fileName,
                size,
                contentType,
                super.getCreatedAt(),
                bytes
        );
    }
}
