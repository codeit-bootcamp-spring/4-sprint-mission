package com.sprint.mission.discodeit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

public class UserStatusDto {

    @AllArgsConstructor
    @Getter
    public static class create {
        private UUID userId;
        private Instant lastActiveAt;
    }

    @AllArgsConstructor
    @Getter
    public static class update {
        Instant newLastActiveAt;
    }

    public record response(
            UUID id,
            UUID userId,
            Instant lastActiveAt,
            Boolean online
    ) {
    }
}
