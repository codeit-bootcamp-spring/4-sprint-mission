package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.LoginType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserStatusResponseDto {
    private UUID id;
    private UUID userId;
    private LoginType loginType;
}
