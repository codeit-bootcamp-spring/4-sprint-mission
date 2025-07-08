package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class BinaryContent {
    // 이미지, 파일 등 바이너리 데이터를 표현하는 도메인 모델.
    // 사용자의 프로필 이미지, 메시지에 첨부된 파일을 저장하기 위해 활용함
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Instant createdAt;

    private String fileName;
    private String fileType;
    private Long fileSize;
    private final byte[] bytes;


    // 수정 불가능한 도메인 모델이기 떄문에 updatedAt 필드는 정의하지 않는다

//    private final UUID messageId; // Message UID
//    private final UUID userId; // User UID

    // User, Message 도메인 모델과의 의존 관계 방향성을 잘 고려하여 id 참조필드 추가하기

    public BinaryContent(String fileName, Long fileSize, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.bytes = data;

        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }
}
