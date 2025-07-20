package com.sprint.mission.discodeit.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 공통 응답 객체의 기본 클래스
 */
@Schema(name = "CommonResponse", description = "공통 응답 객체")
@Getter
public class CommonResponse<T> {
    @Schema(description = "요청 처리 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int code;

    @Schema(description = "클라이언트용 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;

    @Schema(description = "실제 응답 데이터")
    private T data;

    @Schema(description = "응답 발생 시각", example = "2025-07-09T13:45:30")
    private LocalDateTime timestamp;

    public CommonResponse(boolean success, int code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> CommonResponse<T> success(HttpStatus httpStatus, T data) {
        return new CommonResponse<>(true, httpStatus.value() , "요청이 성공적으로 처리되었습니다.", data);
    }

    public static <T> CommonResponse<T> fail(int code, String message, T errorDetail) {
        return new CommonResponse<>(false, code, message, errorDetail);
    }
}
