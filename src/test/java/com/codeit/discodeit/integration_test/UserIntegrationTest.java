package com.codeit.discodeit.integration_test;

import com.codeit.discodeit.auditing_config.AuditingConfig;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserDto;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


@Import(AuditingConfig.class)
class UserIntegrationTest extends IntegrationTestBasic {

  @Autowired
  private UserRepository userRepository;

  private User defaultUser;

  @BeforeEach
  void setUp() {
    // 기존 데이터 초기화
    userRepository.deleteAll();

    // 공통으로 사용할 기본 유저 생성
    defaultUser = User.builder()
        .username("defaultName")
        .email("default@example.com")
        .password("pw1234")
        .profile(null)
        .status(null)
        .build();
    userRepository.saveAndFlush(defaultUser);
  }

  @Test
  void 사용자_생성_성공_테스트() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw1234", "kwon1@example.com", null);

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/users")
            .file(userPart)
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.username").value("kwon1"))
        .andExpect(jsonPath("$.email").value("kwon1@example.com"));
  }

  @Test
  void 필드_검증_실패시_사용자_생성_실패_테스트() throws Exception {
    // given
    UserCreateRequest request = new UserCreateRequest("kwon1", "pw", "kwon1@example.com", null);

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/users")
            .file(userPart)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 유저네임_중복시_사용자_생성_실패_테스트() throws Exception {
    // given

    UserCreateRequest request = new UserCreateRequest("defaultName", "pw1234", "kwon@example.com", null);

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/users")
            .file(userPart)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 유저이메일_중복시_사용자_생성_실패_테스트() throws Exception {
    // given

    UserCreateRequest request = new UserCreateRequest("kwon", "pw1234", "default@example.com", null);

    MockMultipartFile userPart = new MockMultipartFile(
        "userCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/users")
            .file(userPart)
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 유저_수정_성공_테스트() throws Exception {
    // given
    UUID userId = defaultUser.getId();

    // 수정 요청 DTO
    UserUpdateRequest updateRequest = new UserUpdateRequest(
        userId,
        "newName",
        "newEmail@example.com",
        "pw1234"
    );

    MockMultipartFile updateUserPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(updateRequest)
    );

    MockMultipartFile profileImage = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "dummy image content".getBytes()
    );

    // when: PATCH 요청
    ResultActions resultActions = mockMvc.perform(
        multipart(HttpMethod.PATCH, "/api/users/{userId}", userId)
            .file(updateUserPart)
            .file(profileImage)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> { request.setMethod("PATCH"); return request; }) // multipart PATCH 강제
    );

    // then: 응답 검증
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("newName"))
        .andExpect(jsonPath("$.email").value("newEmail@example.com"));
  }

  @Test
  void 필드_검증_실패시_유저_수정_실패_테스트() throws Exception {
    // given

    UserUpdateRequest updateRequest = new UserUpdateRequest(
        UUID.randomUUID(),
        "ne",
        "newEmail@example.com",
        "pw1"
    );

    MockMultipartFile updateUserPart = new MockMultipartFile(
        "userUpdateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(updateRequest)
    );

    MockMultipartFile profileImage = new MockMultipartFile(
        "profile",
        "profile.png",
        MediaType.IMAGE_PNG_VALUE,
        "dummy image content".getBytes()
    );

    // when: PATCH 요청
    ResultActions resultActions = mockMvc.perform(
        multipart(HttpMethod.PATCH, "/api/users/{userId}", UUID.randomUUID())
            .file(updateUserPart)
            .file(profileImage)
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .with(request -> { request.setMethod("PATCH"); return request; }) // multipart PATCH 강제
    );

    // then: 응답 검증
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 유저_삭제_성공_테스트() throws Exception {
    // given
    UUID userId = defaultUser.getId();
    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  void 유저가_없어서_유저_삭제_실패_테스트() throws Exception {
    // given

    UUID userId = UUID.randomUUID();
    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  void 유저_목록_조회_성공_테스트() throws Exception {

    User user1 = User.builder()
        .username("kwon1")
        .email("kwon1@example.com")
        .password("pw1234")
        .profile(null)
        .status(null)
        .build();

    User user2 = User.builder()
        .username("kwon2")
        .email("kwon2@example.com")
        .password("pw1234")
        .profile(null)
        .status(null)
        .build();

    userRepository.save(user1);
    userRepository.save(user2);

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(3)) // 디폴트 유적까지 더해서 3
        .andExpect(jsonPath("$[1].username").value("kwon1"))
        .andExpect(jsonPath("$[1].email").value("kwon1@example.com"))
        .andExpect(jsonPath("$[2].username").value("kwon2"))
        .andExpect(jsonPath("$[2].email").value("kwon2@example.com"));
  }
}