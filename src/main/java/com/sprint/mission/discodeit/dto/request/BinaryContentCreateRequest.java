package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BinaryContentCreateRequest(
    @NotBlank(message = "파일 이름은 공백일 수 없습니다.")
    @Size(min = 3, max = 255, message = "파일 이름은 3자 이상 255자 이하이어야 합니다.")
    String fileName,

    @NotBlank(message = "컨텐트 타입은 공백일 수 없습니다.")
    @Size(min = 3, max = 100, message = "컨텐트 타입은 3자 이상 100자 이하이어야 합니다.")
    String contentType,

    @NotNull(message = "파일 내용은 비어있을 수 없습니다.")
    byte[] bytes
) {

}
