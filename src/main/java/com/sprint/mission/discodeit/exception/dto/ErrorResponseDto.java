package com.sprint.mission.discodeit.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    private int status;
    private String message;
}