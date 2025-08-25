package com.codeit.discodeit.swagger;

import com.codeit.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface SwaggerAuthController {
  @Operation(
      summary = "로그인",
      description = "사용자 로그인 요청",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "로그인 성공",
              content = @Content(
                  mediaType = "application/json",
                  schema = @Schema(implementation = UserDto.class)
              )
          ),
          @ApiResponse(
              responseCode = "400",
              description = "비밀번호가 일치하지 않음",
              content = @Content(
                  mediaType = "*/*",
                  examples = @ExampleObject(value = "Wrong password")
              )
          ),
          @ApiResponse(
              responseCode = "404",
              description = "사용자를 찾을 수 없음",
              content = @Content(
                  mediaType = "*/*",
                  examples = @ExampleObject(value = "User with username {username} not found")
              )
          )
      }
  )
  ResponseEntity<UserDto> loginUser(@RequestBody LoginRequestDto loginRequestDto);
}
