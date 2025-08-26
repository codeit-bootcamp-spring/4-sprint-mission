package com.sprint.mission.discodeit.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockitoBean private UserService userService;

  @MockitoBean private UserStatusService userStatusService;

  @Test
  @DisplayName("POST /api/users - 성공 케이스")
  void createUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "kim", "kim@test.com", null, true);

    // password를 최소 8자로 맞춤
    UserCreateRequest request = new UserCreateRequest("kim", "kim@test.com", "pass1234");

    // userService.create()가 호출될 때 미리 지정한 결과 반환
    given(userService.create(request, Optional.empty())).willReturn(userDto);

    // JSON part 생성
    MockMultipartFile jsonPart =
        new MockMultipartFile(
            "userCreateRequest",
            "userCreateRequest.json",
            MediaType.APPLICATION_JSON_VALUE,
            om.writeValueAsBytes(request));

    // profile 파일 part (선택, 비워도 됨)
    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "profile.png", MediaType.IMAGE_PNG_VALUE, new byte[0]);

    // when & then
    mockMvc
        .perform(
            multipart("/api/users")
                .file(jsonPart)
                .file(profilePart)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 성공 케이스 (Multipart)")
  void updateUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserDto updatedUser = new UserDto(userId, "kim_updated", "kim@test.com", null, true);
    UserUpdateRequest updateRequest =
        new UserUpdateRequest("kim_updated", "kim@test.com", "newPass");

    given(userService.update(userId, updateRequest, Optional.empty())).willReturn(updatedUser);

    MockMultipartFile jsonPart =
        new MockMultipartFile(
            "userUpdateRequest", "", "application/json", om.writeValueAsBytes(updateRequest));

    MockMultipartFile profilePart =
        new MockMultipartFile("profile", "", MediaType.IMAGE_PNG_VALUE, new byte[0]);

    // when & then
    mockMvc
        .perform(
            multipart("/api/users/" + userId)
                .file(jsonPart)
                .file(profilePart)
                .with(
                    request -> {
                      request.setMethod("PATCH");
                      return request;
                    }) // multipart patch hack
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("kim_updated"));
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - 성공 케이스")
  void deleteUser_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    willDoNothing().given(userService).delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/" + userId)).andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /api/users - 성공 케이스")
  void findAllUsers_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserDto userDto = new UserDto(userId, "kim", "kim@test.com", null, true);
    given(userService.findAll()).willReturn(List.of(userDto));

    // when & then
    mockMvc
        .perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(userId.toString()))
        .andExpect(jsonPath("$[0].username").value("kim"));
  }

  @Test
  @DisplayName("PATCH /api/users/{userId}/userStatus - 성공 케이스")
  void updateUserStatus_success() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(now);
    UserStatusDto statusDto = new UserStatusDto(UUID.randomUUID(), userId, now);

    given(userStatusService.updateByUserId(userId, request)).willReturn(statusDto);

    String body = om.writeValueAsString(request);

    // when & then
    mockMvc
        .perform(
            patch("/api/users/" + userId + "/userStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(statusDto.id().toString()))
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.lastActiveAt").value(now.toString()));
  }

  @Test
  @DisplayName("POST /api/users - 실패 케이스 (유효하지 않은 요청)")
  void createUser_fail_invalidRequest() throws Exception {
    // given: 잘못된 요청 (username이 null)
    UserCreateRequest badRequest = new UserCreateRequest(null, "invalid@test.com", "pass123");

    MockMultipartFile jsonPart =
        new MockMultipartFile(
            "userCreateRequest", "", "application/json", om.writeValueAsBytes(badRequest));

    // when & then
    mockMvc
        .perform(
            multipart("/api/users")
                .file(jsonPart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/users/{userId} - 실패 케이스 (존재하지 않는 사용자)")
  void updateUser_fail_notFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest updateRequest = new UserUpdateRequest("noname", "no@test.com", "pass");
    given(userService.update(eq(userId), eq(updateRequest), any()))
        .willThrow(new RuntimeException("User not found"));

    MockMultipartFile jsonPart =
        new MockMultipartFile(
            "userUpdateRequest", "", "application/json", om.writeValueAsBytes(updateRequest));

    // when & then
    mockMvc
        .perform(
            multipart("/api/users/" + userId)
                .file(jsonPart)
                .with(
                    req -> {
                      req.setMethod("PATCH");
                      return req;
                    })
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()); // 예외 전파
  }

  @Test
  @DisplayName("DELETE /api/users/{userId} - 실패 케이스 (존재하지 않는 사용자)")
  void deleteUser_fail_notFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    willThrow(new RuntimeException("User not found")).given(userService).delete(userId);

    // when & then
    mockMvc.perform(delete("/api/users/" + userId)).andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("PATCH /api/users/{userId}/userStatus - 실패 케이스 (잘못된 요청)")
  void updateUserStatus_fail_invalidRequest() throws Exception {
    // given: newLastActiveAt = null
    String body = """
        {
          "newLastActiveAt": null
        }
        """;

    // when & then
    mockMvc
        .perform(
            patch("/api/users/" + UUID.randomUUID() + "/userStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details").exists()); // 에러 응답에 errors 필드가 포함되었는지 확인
  }

  @Test
  @DisplayName("PATCH /api/users/{userId}/userStatus - 실패 케이스 (존재하지 않는 사용자)")
  void updateUserStatus_fail_notFound() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    Instant now = Instant.now();
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(now);

    given(userStatusService.updateByUserId(userId, request))
        .willThrow(new RuntimeException("User not found"));

    String body = om.writeValueAsString(request);

    // when & then
    mockMvc
        .perform(
            patch("/api/users/" + userId + "/userStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }
}
