package com.sprint.mission.discodeit.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ErrorResponse {

  @Schema(description = "The error code", example = "404", format = "int")
  private final int status;
  @Schema(description = "The error message", example = "User does not exist")
  private final String message;
  @Schema(description = "The timestamp of when the error occurred (in milliseconds)", example = "1720427444000")
  private final long timestamp;

  public ErrorResponse(int status, String message) {
    this.status = status;
    this.message = message;
    this.timestamp = System.currentTimeMillis();
  }

}
