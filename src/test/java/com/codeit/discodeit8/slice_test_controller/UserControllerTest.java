package com.codeit.discodeit8.slice_test_controller;

import com.codeit.discodeit8.controller.UserController;
import com.codeit.discodeit8.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit8.dto.user_service_dto.UserDto;
import com.codeit.discodeit8.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit8.exception.global.GlobalExceptionHandler;
import com.codeit.discodeit8.exception.user.UserNotFoundException;
import com.codeit.discodeit8.service.UserService;
import com.codeit.discodeit8.service.UserStatusService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;



import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import({GlobalExceptionHandler.class, UserConfig.class})
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService; // 인터페이스 타입 Mock

  @Autowired
  private UserStatusService userStatusService; // 인터페이스 타입 Mock

  @Test
  void 유저_생성_API_요청_성공_테스트() throws Exception {
    // given

    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        """
        {
          "username": "testuser",
          "password": "pw1234",
          "email": "testuser@example.com"
        }
        """.getBytes()
    );

    UserDto mockUserDto = new UserDto(
        UUID.randomUUID(),
        "testuser",
        "test@example.com",
        null,
        true
    );

    // given: 서비스 호출 시 결과 설정
    given(userService.createUser(Mockito.any(UserCreateRequest.class)))
        .willReturn(mockUserDto);

    // when
    ResultActions resultActions = mockMvc.perform(multipart("/api/users")
        .file(userCreateRequestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("testuser"))
        .andExpect(jsonPath("$.email").value("test@example.com"));
  }
  
  @Test
  void 검증이_필요한_필드들의_값이_짧을떄_유저_생성_API_요청_실패_테스트() throws Exception {
    // given
    MockMultipartFile userCreateRequestPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        """
        {
          "username": "tes",
          "password": "pw1",
          "email": "t@e.com"
        }
        """.getBytes()
    ); // 각 필드 값들이 길이가 더 길어야 검증이 성공된다.

    // when
    ResultActions resultActions = mockMvc.perform(multipart("/api/users")
        .file(userCreateRequestPart)
        .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FAILED_VALIDATION_ERROR"));

  }

  @Test
  void 유저_삭제_API_요청_성공_테스트() throws Exception {
    // given
    when(userService.findUserDtoByUserId(Mockito.any(UUID.class))).thenReturn(new UserDto(null, null, null, null, null));

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/users/{userId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  void 유저_삭제_API_요청_실패_테스트() throws Exception {
    // given
    when(userService.findUserDtoByUserId(any(UUID.class)))
        .thenThrow(new UserNotFoundException(null));

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/users/{userId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
  }

  @Test
  void 검증이_필요한_필드들의_값이_짧을떄_유저_수정_API_요청_실패_테스트() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // 유효성 실패: newUsername 3글자, newEmail 형식 잘못, newPassword 3글자
    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "userUpdateRequest.json",
        MediaType.APPLICATION_JSON_VALUE,
        """
                {
                  "newUsername": "abc",
                  "newEmail": "x@",
                  "newPassword": "123"
                }
                """.getBytes()
    );

    ResultActions resultActions = mockMvc.perform(multipart("/api/users/{userId}", userId)
        .file(userUpdateRequestPart)
        .with(request -> { request.setMethod("PATCH"); return request; })
        .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    resultActions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FAILED_VALIDATION_ERROR"))
        .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("검증이 필요한 필드")))
        .andExpect(jsonPath("$.details.fieldErrors.newUsername").exists())
        .andExpect(jsonPath("$.details.fieldErrors.newEmail").exists())
        .andExpect(jsonPath("$.details.fieldErrors.newPassword").exists());
  }

  @Test
  void 유저_수정_API_요청_성공_테스트() throws Exception {
    // given
    UUID userId = UUID.randomUUID();

    // Mock JSON part: 모든 필드 유효
    MockMultipartFile userUpdateRequestPart = new MockMultipartFile(
        "userUpdateRequest",
        "userUpdateRequest.json",
        MediaType.APPLICATION_JSON_VALUE,
        """
                {
                  "newUsername": "updatedUser",
                  "newEmail": "updated@example.com",
                  "newPassword": "newpw"
                }
                """.getBytes()
    );

    // Mock 프로필 파일 (선택)
    MockMultipartFile profile = new MockMultipartFile(
        "profile",
        "profile.jpg",
        MediaType.IMAGE_JPEG_VALUE,
        "dummy image".getBytes()
    );

    // 서비스가 반환할 DTO
    UserDto mockUpdatedUser = new UserDto(
        userId,
        "updatedUser",
        "updated@example.com",
        null,
        true
    );

    given(userService.updateUser(any(UserUpdateRequest.class), any(), any()))
        .willReturn(mockUpdatedUser);

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/users/{userId}", userId)
            .file(userUpdateRequestPart)
            .file(profile)
            .with(request -> { request.setMethod("PATCH"); return request; })
            .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then: JSON 응답 검증
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.username").value("updatedUser"))
        .andExpect(jsonPath("$.email").value("updated@example.com"));
  }
}
// TODO Auditing 문제가 생김
// 추후에 메인에는 따로 적용할 수 있도록 해야할듯