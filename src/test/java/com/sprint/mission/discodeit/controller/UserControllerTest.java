package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  UserService userService;

  @MockitoBean
  UserStatusService userStatusService;

  @Autowired
  private ObjectMapper objectMapper;


  /**
   * POST - create
   *
   * @throws Exception
   */
  @DisplayName("유저 생성 테스트")
  @Test
  void postUser_returnsUserDto() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    UserCreateRequest userCreateRequestDto = new UserCreateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );
    String userCreateRequestJson = objectMapper.writeValueAsString(userCreateRequestDto);

    // 💡 @RequestPart("userCreateRequest")
    // the endpoint only accepts requests
    // formatted as multipart/form-data
    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest", // must match the @RequestPart name in the controller
        "",
        MediaType.APPLICATION_JSON_VALUE,
        userCreateRequestJson.getBytes()
    );

    // 💡 @RequestPart(value = "profile", required = false)
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", // matching the @RequestPart name
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // The DTO that the mocked service will return
    UserDto mockResponseDto = new UserDto(
        UUID.randomUUID(),
        "Bob",
        "bob@gmail.com",
        new BinaryContentDto(
            UUID.randomUUID(),
            "profile.png",
            (long) "test-image-data".getBytes().length,
            MediaType.IMAGE_PNG_VALUE
        ),
        true
    );

    when(userService.create(any(UserCreateRequest.class), any(Optional.class)))
        .thenReturn(mockResponseDto);

    // ================== when & ==================

    mockMvc.perform(multipart("/api/users") // Use the multipart builder
            .file(userCreateRequestPart)  // Add the JSON part
            .file(profilePart))         // Add the file part
        .andExpect(status().isCreated()) // Check for 201 Created status
        .andExpect(jsonPath("$.username").value("Bob")) // Also check the response body
        .andExpect(jsonPath("$.email").value("bob@gmail.com"))
        .andExpect(jsonPath("$.profile").exists())
        .andExpect(jsonPath("$.online").value(true));
  }

  @DisplayName("유저 생성 테스트 실패 - 잘못된 요청")
  @Test
  void postUser_withMissingRequiredPart_Failure() throws Exception {
    // bad request because we didn't include userCreateRequestDto
    // ================== given ==================

    // 💡 @RequestPart(value = "profile", required = false)
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", // matching the @RequestPart name
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // ================== when & then ==================

    mockMvc.perform(multipart("/api/users") // Use the multipart builder
            .file(profilePart))         // Add the file part
        .andExpect(status().isBadRequest());
  }

  /**
   * PATCH - edit
   *
   * @throws Exception
   */
  @DisplayName("유저 수정 테스트")
  @Test
  void patchUser_returnsUserDto() throws Exception {
    // ================== given ==================

    UUID userId = UUID.randomUUID();
    // The DTO for the request body
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "Bob",
        "bob@gmail.com",
        "pw123"
    );
    String userUpdateRequestJson = objectMapper.writeValueAsString(userUpdateRequest);

    // 💡 @RequestPart("userCreateRequest")
    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest", // must match the @RequestPart name in the controller
        "",
        MediaType.APPLICATION_JSON_VALUE,
        userUpdateRequestJson.getBytes()
    );

    // 💡 @RequestPart(value = "profile", required = false)
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", // matching the @RequestPart name
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // The DTO that the mocked service will return
    UserDto mockResponseDto = new UserDto(
        UUID.randomUUID(),
        "Bob",
        "bob@gmail.com",
        new BinaryContentDto(
            UUID.randomUUID(),
            "profile.png",
            (long) "test-image-data".getBytes().length,
            MediaType.IMAGE_PNG_VALUE
        ),
        true
    );

    when(userService.update(any(UUID.class), any(UserUpdateRequest.class), any(Optional.class)))
        .thenReturn(mockResponseDto);

    // ================== when & ==================

    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId) // Use the multipart builder
            .file(userUpdateRequestPart)  // Add the JSON part
            .file(profilePart))         // Add the file part
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("Bob")) // Also check the response body
        .andExpect(jsonPath("$.email").value("bob@gmail.com"))
        .andExpect(jsonPath("$.profile").exists())
        .andExpect(jsonPath("$.online").value(true));

  }

  @DisplayName("유저 수정 테스트 실패 - 잘못된 요청")
  @Test
  void patchUser_withMissingRequiredPart_Failure() throws Exception {
    // bad request because we didn't include userUpdateRequestDto
    // ================== given ==================
    UUID userId = UUID.randomUUID();
    // 💡 @RequestPart(value = "profile", required = false)
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", // matching the @RequestPart name
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // ================== when & then ==================
    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId) // Use the multipart builder
            .file(profilePart))         // Add the file part
        .andExpect(status().isBadRequest());
  }

  @DisplayName("유저 수정 테스트 실패 - 잘못된 요청 (Validation error)")
  @Test
  void patchUser_withValidationError_Failure() throws Exception {
    UUID userId = UUID.randomUUID();
    // The DTO for the request body
    UserUpdateRequest userUpdateRequest = new UserUpdateRequest(
        "", // <<<<<< this causes validation error
        "bob@gmail.com",
        "pw123"
    );
    String userUpdateRequestJson = objectMapper.writeValueAsString(userUpdateRequest);

    // 💡 @RequestPart("userCreateRequest")
    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest", // must match the @RequestPart name in the controller
        "",
        MediaType.APPLICATION_JSON_VALUE,
        userUpdateRequestJson.getBytes()
    );

    // 💡 @RequestPart(value = "profile", required = false)
    MockMultipartFile profilePart = new MockMultipartFile(
        "profile", // matching the @RequestPart name
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // ================== when & ==================

    mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/" + userId) // Use the multipart builder
            .file(userUpdateRequestPart)
            .file(profilePart))
        .andExpect(status().isBadRequest());
  }

  /**
   * DELETE
   *
   * @throws Exception
   */
  @DisplayName("유저 삭제 테스트 실패")
  @Test
  void deleteUser_returnsVoid() throws Exception {
    // ================== given ==================
    UUID userId = UUID.randomUUID();
    doNothing().when(userService).delete(userId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/users/" + userId))
        .andExpect(status().isNoContent());
  }

  @DisplayName("유저 삭제 테스트 실패 - 유저 존재하지 않음")
  @Test
  void deleteUser_userNotFound_Failure() throws Exception {
    // ================== given ==================
    UUID userId = UUID.randomUUID();
    doThrow(new UserNotFoundException(userId))
        .when(userService)
        .delete(userId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/users/" + userId))
        .andExpect(status().isNotFound());
  }

  /**
   * GET - findAll
   *
   * @throws Exception
   */
  @DisplayName("모든 유저 조회 테스트")
  @Test
  void findAllUsers_returnsUserList() throws Exception {
    // ================== given ==================
    UserDto userDto = new UserDto(
        UUID.randomUUID(),
        "Bob",
        "bob@gmail.com",
        null,
        true
    );
    List<UserDto> userDtoList = List.of(userDto);
    when(userService.findAll())
        .thenReturn(userDtoList);

    // ================== when & then ==================
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk());
  }

  @DisplayName("모든 유저 조회 테스트 - 유저 없음")
  @Test
  void findAllUsers_returnsUserList_None() throws Exception {
    // ================== given ==================
    when(userService.findAll())
        .thenReturn(List.of());

    // ================== when & then ==================
    mockMvc.perform(get("/api/users"))
        .andExpect(status().isOk());
  }

  /**
   * PATCH - updateUserStatusByUserId
   *
   * @throws Exception
   */
  @DisplayName("유저 Id로 UserStatus 업데이트 ")
  @Test
  void updateUserStatusByUserId_returnsUserStatusDto() throws Exception {
    // ================== given ==================
    UUID userId = UUID.randomUUID();
    Instant newLastActiveAt = Instant.now().minusSeconds(10);
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(
        newLastActiveAt
    );

    UserStatusDto expectedUserStatusDto = new UserStatusDto(
        UUID.randomUUID(),
        userId,
        newLastActiveAt
    );

    when(userStatusService.updateByUserId(userId, request))
        .thenReturn(expectedUserStatusDto);

    // ================== when & then ==================
    mockMvc.perform(patch("/api/users/" + userId + "/userStatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
        .andExpect(jsonPath("$.lastActiveAt").value(newLastActiveAt.toString()));
  }

  @DisplayName("유저 Id로 UserStatus 업데이트 실패 - Validation Error")
  @Test
  void updateUserStatusByUserId_invalidFields_Failure() throws Exception {
    // ================== given ==================
    UUID userId = UUID.randomUUID();
    Instant newLastActiveAt = Instant.now().plusSeconds(100); // future date, fails validation
    UserStatusUpdateRequest request = new UserStatusUpdateRequest(
        newLastActiveAt
    );

    UserStatusDto expectedUserStatusDto = new UserStatusDto(
        UUID.randomUUID(),
        userId,
        newLastActiveAt
    );

    when(userStatusService.updateByUserId(userId, request))
        .thenReturn(expectedUserStatusDto);

    // ================== when & then ==================
    mockMvc.perform(patch("/api/users/" + userId + "/userStatus")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
