package com.sprint.mission.discodeit.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ErrorResponseDto {
    private int status;
    private String message;

    @AllArgsConstructor
    @Getter
    public static class ErrorResponseGroupDto {
        private int status;
        private Map<String, String> groupErrors;
    }
}
