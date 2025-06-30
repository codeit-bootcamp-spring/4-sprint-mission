package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
* 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델입니다.
* 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용합니다.
* 수정이 불가능합니다.
* */
@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;
    private final byte[] bytes;
    private final BinaryContentType type;

    public BinaryContent(byte[] bytes, BinaryContentType type) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();

        this.bytes = bytes;
        this.type = type;
    }
}
