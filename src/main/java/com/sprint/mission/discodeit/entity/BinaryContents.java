package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.io.File;

@Getter
@Setter
public class BinaryContents extends BaseEntity {
    private UUID referenceId;
    private String binaryContentsPath;
    private final BinaryContentType binaryContentType;
    private final byte[] binaryData;

    public BinaryContents(String binaryContentsPath, BinaryContentType binaryContentType, byte[] binaryData) {
        this.binaryContentsPath = binaryContentsPath;
        this.binaryContentType = binaryContentType;
        this.binaryData = binaryData;
    }


    public BinaryContents(UUID referenceId, String binaryContentsPath, BinaryContentType binaryContentType) {
        this.referenceId = referenceId;
        this.binaryContentsPath = binaryContentsPath;
        this.binaryContentType = binaryContentType;

        // 📥 파일 경로에서 byte[] 읽어오기
        try (FileInputStream fis = new FileInputStream(new File(binaryContentsPath))) {
            this.binaryData = fis.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("파일을 읽는 데 실패했습니다: " + binaryContentsPath, e);
        }
    }
    // 생성자에서 주입 안하는 이유는 유저를 생성할려면 프로필 아이디를 먼저 생성해야하기 때문
}

// 이것이 메시지를 위한 바이너리 컨텐츠인지 유저 프로필을 위한 바이너리 컨텐츠인지 어떻게 알지? -> enum 으로
