package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.LoginRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.response.ResponseDto;
import com.sprint.mission.discodeit.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "로그인", description = "이름, 비밀번호로 로그인")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "로그인 성공",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  {
                      "username": "test",
                      "email": "test@codeit.com",
                      "password": "testPw",
                      "profileId": "cd5da335-e8ea-4764-9563-1c10bd1e1938",
                      "id": "59cdbeac-c530-47d8-905c-90fdaaf91884",
                      "createdAt": "2025-07-14T05:23:03.776183Z",
                      "updatedAt": null
                  }
                  """)
          )
      ),
      @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  User with username {username} not found
                  """)
          )),
      @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않음",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseDto.class),
              examples = @ExampleObject(value = """
                  Wrong password
                  """)
          ))
  })
  @RequestMapping(value = "/api/auth/login", method = RequestMethod.POST)
  public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {
    User user = authService.login(loginRequest);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(user);
  }
}
