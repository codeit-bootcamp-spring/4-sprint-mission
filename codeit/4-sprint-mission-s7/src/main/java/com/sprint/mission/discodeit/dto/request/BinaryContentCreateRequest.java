package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일명은 공백일 수 없습니다")
    @Size(max = 255, message = "파일명은 최대 255자까지 허용됩니다")
    String fileName,

    @NotBlank(message = "콘텐츠 타입은 비어 있을 수 없습니다.")
    @Pattern(
            regexp = "^[\\w!#$&^_.+-]+/[\\w!#$&^_.+-]+$",
            message = "contentType은 유효한 MIME 타입이어야 합니다(예: image/png).")
    String contentType,

    @NotNull(message = "바이트 배열은 null일 수 없습니다.")
    @Size(min = 1, message = "바이트 배열은 비어 있을 수 없습니다.")
    byte[] bytes
) {

}
