package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.data.UserStatusDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private UserStatusService userStatusService;

    @Test
    @DisplayName("유저 생성 성공 - 201 Created, 생성 유저 정보 반환")
    void createUserSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserCreateRequest request = new UserCreateRequest("username", "email@codeit.com", "password");
        UserDto responseDto = new UserDto(userId, "username", "email@codeit.com", null, true);

        given(userService.create(any(UserCreateRequest.class), eq(Optional.empty()))).willReturn(responseDto);

        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        ResultActions actions = mvc.perform(
                multipart("/api/users")
                        .file(userPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(request.username()))
                .andExpect(jsonPath("$.email").value(request.email()));
    }

    @Test
    @DisplayName("유저 생성 실패 - 필수 필드(username) 누락으로 404 반환")
    void createUserFailureMissingNameField() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest(null, "email@codeit.com", "password");

        MockMultipartFile userPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when
        ResultActions actions = mvc.perform(
                multipart("/api/users")
                        .file(userPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("user 업데이트 성공 - 200 Ok, user update 정보 반환")
    void updateUserSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newUsernama", "newEmail@codeit.com", "newPassword");
        UserDto userDto = new UserDto(userId, userUpdateRequest.newUsername(), userUpdateRequest.newEmail(), null, true);
        given(userService.update(eq(userId), any(UserUpdateRequest.class), eq(Optional.empty()))).willReturn(userDto);

        MockMultipartFile userPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userUpdateRequest)
        );

        // when
        ResultActions actions = mvc.perform(
                multipart("/api/users/{userId}", userId)
                        .file(userPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value(userUpdateRequest.newUsername()))
                .andExpect(jsonPath("$.email").value(userUpdateRequest.newEmail()));
    }

    @Test
    @DisplayName("user 업데이트 실패 - 잘못된 파라미터(userId)로 인해 user 찾을 수 없음")
    void updateUserFailCasedByWrongUserId() throws Exception {
        // given
        UUID wrongUserId = UUID.randomUUID();
        UserUpdateRequest userUpdateRequest =
                new UserUpdateRequest("newName", "newEmail@codeit.com", "newPassword");

        given(userService.update(eq(wrongUserId), any(UserUpdateRequest.class), any()))
                .willThrow(new UserNotFoundException(Map.of("userId", wrongUserId.toString())));

        MockMultipartFile userPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(userUpdateRequest)
        );

        // when
        ResultActions actions = mvc.perform(
                multipart("/api/users/{userId}", wrongUserId)
                        .file(userPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("userId로 userStatus 업데이트 성공 - 200 OK, 업데이트된 유저 정보 반환")
    void updateUserStatusSuccess() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
        UserStatusDto userStatusDto = new UserStatusDto(UUID.randomUUID(), userId, Instant.now());

        given(userStatusService.updateByUserId(eq(userId), any(UserStatusUpdateRequest.class)))
                .willReturn(userStatusDto);

        // when
        ResultActions actions = mvc.perform(
                patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userStatusDto.id().toString()))
                .andExpect(jsonPath("$.userId").value(userStatusDto.userId().toString()));
    }

    @Test
    @DisplayName("userStatus 업데이트 실패  -  요청 바디가 없어서 400")
    void updateUserStatusFailCausedByEmptyBody() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when
        ResultActions actions = mvc.perform(
                patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")
                        .accept(MediaType.APPLICATION_JSON)
        );

        // thwn
        actions.andExpect(status().is5xxServerError());
    }
}