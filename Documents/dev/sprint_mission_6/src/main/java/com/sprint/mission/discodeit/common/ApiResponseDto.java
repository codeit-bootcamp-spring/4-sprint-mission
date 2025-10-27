package com.sprint.mission.discodeit.common;

import com.sprint.mission.discodeit.exception.StatusCode;
import lombok.Getter;

@Getter
public class ApiResponseDto<T> {

    private String code; //  상태 코드 키 ex) "USER_CREATE"
    private String message; //클라이언트용 응답 메시지 ex) "User가 성공적으로 생성됨"
    private T data; //실질적인 응답 데이터 ex) UserCreateDto, MessageResponseDto 등

    public ApiResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> success(StatusCode statusCode, T data) {
        return new ApiResponseDto<>(statusCode.name(), statusCode.getMessage(), data);
    }

    public static <T> ApiResponseDto<T> success(String code, String message, T data) {
        return new ApiResponseDto<>(code,message,data);
    }

}
