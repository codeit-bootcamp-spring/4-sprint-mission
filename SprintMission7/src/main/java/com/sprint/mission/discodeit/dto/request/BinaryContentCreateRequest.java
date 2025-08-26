package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "fileName은 필수이며 빈 값일 수 없습니다.")
        @Size(max = 255, message = "fileName은 최대 255자까지 가능합니다.")
        String fileName,
    @NotBlank(message = "contentType은 필수이며 빈 값일 수 없습니다.")
        @Size(max = 100, message = "contentType은 최대 100자까지 가능합니다.")
        String contentType,
    @NotNull(message = "bytes는 필수이며 비어있을 수 없습니다.") @Size(min = 1, message = "bytes는 빈 배열일 수 없습니다.")
        byte[] bytes) {}
