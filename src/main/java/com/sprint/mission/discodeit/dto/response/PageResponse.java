package com.sprint.mission.discodeit.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Page Response")
public record PageResponse<T>(
        @Schema(description = "내용")
        List<T> content,
        @Schema
        Object nextCursor,
        @Schema(type = "integer", format = "int32")
        int size,
        @Schema(type = "boolean")
        boolean hasNext,
        @Schema(type = "integer", format = "int64")
        Long totalElements
) {
}
