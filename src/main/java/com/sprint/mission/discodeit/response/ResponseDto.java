package com.sprint.mission.discodeit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ResponseDto<T> {
  @Schema(description = "요청 성공 여부", example = "true")
  private boolean success;

  @Schema(description = "응답 데이터")
  private T data;

  @Schema(description = "에러 정보", nullable = true)
  private ApiError error;
}
