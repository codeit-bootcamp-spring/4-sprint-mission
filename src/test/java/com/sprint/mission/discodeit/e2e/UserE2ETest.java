package com.sprint.mission.discodeit.e2e;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class UserE2ETest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        var httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.createDefault();
        var factory = new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(httpClient);
        rest.getRestTemplate().setRequestFactory(factory);
    }

    @AfterTransaction
    void cleanUp() {
        userRepository.deleteAll();
    }

    @DisplayName("유저 생성")
    @Test
    void createUser() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("userCreateRequest", userCreateRequest);

        // when - POST /api/users
        ResponseEntity<UserDto> created = rest.postForEntity("/api/users", request, UserDto.class);

        // then - 201 + id 추가 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID id = created.getBody().id();
        assertThat(id).isNotNull();
    }

    @DisplayName("유저 수정")
    @Test
    void updateUser() {
        // 유저 생성 & given 생성 정보
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> CreateRequest = new LinkedMultiValueMap<>();
        CreateRequest.add("userCreateRequest", userCreateRequest);
        CreateRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> created = rest.postForEntity("/api/users", CreateRequest, UserDto.class);

        // then - 201 + 생성 정보 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID userId = created.getBody().id();
        assertThat(userId).isNotNull();
        assertThat(created.getBody().username()).isEqualTo(userCreateRequest.username());
        assertThat(created.getBody().email()).isEqualTo(userCreateRequest.email());

        // given - 수정할 정보
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newUsername", "new@email.com", "newPassword");
        MultiValueMap<String, Object> updateRequest = new LinkedMultiValueMap<>();
        updateRequest.add("userUpdateRequest", userUpdateRequest);
        updateRequest.add("profile", null);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(updateRequest);

        // when PATCH /api/users/{user-id}
        ResponseEntity<UserDto> updated = rest.exchange("/api/users/" + userId, HttpMethod.PATCH, request, UserDto.class);

        // then 수정된 유저 정보 - 200 + id 및 수정 정보 확인
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().id()).isEqualTo(userId);
        assertThat(updated.getBody().username()).isEqualTo(userUpdateRequest.newUsername());
        assertThat(updated.getBody().email()).isEqualTo(userUpdateRequest.newEmail());
    }

    @DisplayName("유저 삭제")
    @Test
    void deleteUser() {
        // 유저 생성 & given 생성 정보
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> CreateRequest = new LinkedMultiValueMap<>();
        CreateRequest.add("userCreateRequest", userCreateRequest);
        CreateRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> created = rest.postForEntity("/api/users", CreateRequest, UserDto.class);

        // then - 201 + 생성 정보 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID userId = created.getBody().id();
        assertThat(userId).isNotNull();
        assertThat(created.getBody().username()).isEqualTo(userCreateRequest.username());
        assertThat(created.getBody().email()).isEqualTo(userCreateRequest.email());

        // and when - GET /api/users , response = ResponseEntity<List<UserDto>>
        ResponseEntity<List> allUsers = rest.getForEntity("/api/users", List.class);

        // and then - 200 + 유저 수 확인
        assertThat(allUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allUsers.getBody()).isNotNull();
        assertThat(allUsers.getBody().size()).isEqualTo(1);

        // and when - DELETE /api/users/{user-id}, response = ResponseEntity , body 없음
        ResponseEntity response = rest.exchange("/api/users/" + userId, HttpMethod.DELETE, HttpEntity.EMPTY, ResponseEntity.class);
        allUsers = rest.getForEntity("/api/users", List.class); // 유저 조회

        // and then - 203 + body 없음 및 유저 수 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(allUsers.getBody()).isEmpty();
        assertThat(allUsers.getBody().size()).isEqualTo(0);
    }

    @DisplayName("유저 조회")
    @Test
    void findAllUser() {
        // 유저 생성 & given 생성 정보
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> CreateRequest = new LinkedMultiValueMap<>();
        CreateRequest.add("userCreateRequest", userCreateRequest);
        CreateRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> created = rest.postForEntity("/api/users", CreateRequest, UserDto.class);

        // then - 201 + 생성 정보 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID userId = created.getBody().id();
        assertThat(userId).isNotNull();
        assertThat(created.getBody().username()).isEqualTo(userCreateRequest.username());
        assertThat(created.getBody().email()).isEqualTo(userCreateRequest.email());

        // and when - 유저 조회 GET /api/users , response = ResponseEntity<List<UserDto>>
        ResponseEntity<List> allUsers = rest.getForEntity("/api/users", List.class);

        // and then - 200 + 유저 수 확인
        assertThat(allUsers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(allUsers.getBody()).isNotNull();
        assertThat(allUsers.getBody().size()).isEqualTo(1);
    }

    @DisplayName("동일한 유저네임이나 동일한 이메일로 유저 생성을 요청하면 생성에 실패한다.")
    @Test
    void createShouldFailedWhenUsernameOrEmailAlreadyExists() {
        // given
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("userCreateRequest", userCreateRequest);

        UserCreateRequest userCreateRequestDuplicateEmail = new UserCreateRequest("newname", "password", "test@email.com");
        MultiValueMap<String, Object> requestDuplicateEmail = new LinkedMultiValueMap<>();
        requestDuplicateEmail.add("userCreateRequest", userCreateRequestDuplicateEmail);

        UserCreateRequest userCreateRequestDuplicateUsername = new UserCreateRequest("username", "password", "new@email.com");
        MultiValueMap<String, Object> requestDuplicateUsername = new LinkedMultiValueMap<>();
        requestDuplicateUsername.add("userCreateRequest", userCreateRequestDuplicateUsername);

        // when - POST /api/users
        ResponseEntity<UserDto> created = rest.postForEntity("/api/users", request, UserDto.class);
        ResponseEntity<ErrorResponse> duplicateEmailFailed = rest.postForEntity("/api/users", requestDuplicateEmail, ErrorResponse.class);
        ResponseEntity<ErrorResponse> duplicateUsernameFailed = rest.postForEntity("/api/users", requestDuplicateUsername, ErrorResponse.class);

        // then - 400 응답 + Exception 종류 확인
        assertThat(duplicateEmailFailed.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(duplicateEmailFailed.getBody()).isNotNull();
        assertThat(duplicateEmailFailed.getBody().getCode()).isEqualTo(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS.name());

        // and then - 400 응답 + Exception 종류 확인
        assertThat(duplicateUsernameFailed.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(duplicateUsernameFailed.getBody()).isNotNull();
        assertThat(duplicateUsernameFailed.getBody().getCode()).isEqualTo(ErrorCode.EMAIL_OR_USERNAME_ALREADY_EXISTS.name());
    }

    @DisplayName("PathVariable로 받는 Id에 해당하는 유저가 존재하지 않으면 실패한다.")
    @Test
    void shouldFailedWhenUserNotFound() {
        // update의 경우
        // given - 수정할 정보
        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("newUsername", "new@email.com", "newPassword");
        MultiValueMap<String, Object> updateRequest = new LinkedMultiValueMap<>();
        updateRequest.add("userUpdateRequest", userUpdateRequest);
        updateRequest.add("profile", null);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(updateRequest);

        // when - PATCH /api/users/{user-id}
        ResponseEntity<ErrorResponse> updated = rest.exchange("/api/users/" + UUID.randomUUID(), HttpMethod.PATCH, request, ErrorResponse.class);

        // then - 404 응답 + Exception 종류 확인
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());

        // delete의 경우
        // and when DELETE /api/users/{user-id}
        ResponseEntity<ErrorResponse> deleted = rest.exchange("/api/users/" + UUID.randomUUID(), HttpMethod.DELETE, request, ErrorResponse.class);

        // and then - 404 응답 + Exception 종류 확인
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(deleted.getBody()).isNotNull();
        assertThat(deleted.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }
}
