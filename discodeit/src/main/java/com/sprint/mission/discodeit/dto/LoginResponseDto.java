package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private UUID userId;
    private String username;
    private String email;
    private BinaryContentDto profilePicture;
    private Instant loginTime;
}
