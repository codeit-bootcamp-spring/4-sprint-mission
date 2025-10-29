package com.sprint.mission.discodeit.entity;

import lombok.Getter;

@Getter
public enum BinaryContentStatus {
    PROCESSING("업로드 중"),
    SUCCESS("업로드 완료"),
    FAIL("업로드 실패");

    private final String description;

    BinaryContentStatus(String description) {
        this.description = description;
    }
}
