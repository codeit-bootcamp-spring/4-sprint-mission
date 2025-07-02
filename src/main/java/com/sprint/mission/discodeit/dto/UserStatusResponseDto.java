package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 비밀번호 제외, 온라인 상태 포함
@Getter
@AllArgsConstructor
public class UserStatusResponseDto {

    private UUID id;
    private String username;
    private String email;
    private UUID profileImageId;
    private Instant updatedAt;
}
