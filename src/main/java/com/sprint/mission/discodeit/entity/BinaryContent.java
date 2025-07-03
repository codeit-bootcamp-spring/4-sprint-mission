package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;
import java.io.File;

@Getter
@Setter
public class BinaryContent extends BaseEntity {
    private UUID referenceId;
    private String binaryContentPath;
    private final BinaryContentType binaryContentType;
    private final byte[] bytes;

    public BinaryContent(String binaryContentPath, BinaryContentType binaryContentType, byte[] bytes) {
        this.binaryContentPath = binaryContentPath;
        this.binaryContentType = binaryContentType;
        this.bytes = bytes;
    }

    public BinaryContent(UUID referenceId, String binaryContentPath, BinaryContentType binaryContentType) {
        this.referenceId = referenceId;
        this.binaryContentPath = binaryContentPath;
        this.binaryContentType = binaryContentType;

        // 📥 파일 경로에서 byte[] 읽어오기
        try (FileInputStream fis = new FileInputStream(new File(binaryContentPath))) {
            this.bytes = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 데 실패했습니다: " + binaryContentPath, e);
        }
    }
    // 생성자에서 주입 안하는 이유는 유저를 생성할려면 프로필 아이디를 먼저 생성해야하기 때문
}