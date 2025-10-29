package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.constant.UserErrorCode;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.service.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("기본 프로필을 가진 유저를 생성한다")
    void createUserWithoutProfile() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        User user = new User("test","test@test.com","test",null);
        UUID uuid = UUID.randomUUID();
        ReflectionTestUtils.setField(user, "id", uuid);
        given(userService.createUser(any(UserCreateServiceRequest.class))).willReturn(new UserResponse(user));

        //when & then
        mockMvc.perform(multipart("/api/users")
                        .file(new MockMultipartFile("userCreateRequest", "",
                                MediaType.APPLICATION_JSON_VALUE,
                                objectMapper.writeValueAsString(userCreateRequest).getBytes())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.profile").doesNotExist());
    }

    @Test
    @DisplayName("프로필과 함께 유저를 생성한다")
    void createUserWithProfile() throws Exception {
        //given
        MockMultipartFile profile = new MockMultipartFile("file", "test-image.jpg", "image/jpeg", "content".getBytes());
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null );
        User user = new User("test","test@test.com","test",null);
        UUID uuid = UUID.randomUUID();

        user.updateProfile(new BinaryContent("file", "image/jpeg", profile.getSize(), "content".getBytes()));
        ReflectionTestUtils.setField(user, "id", uuid);
        given(userService.createUser(any(UserCreateServiceRequest.class))).willReturn(new UserResponse(user));

        //when & then
        mockMvc.perform(multipart("/api/users")
                        .file(new MockMultipartFile("userCreateRequest", "",
                                "application/json",
                                objectMapper.writeValueAsString(userCreateRequest).getBytes())
                        )
                        .file(profile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.profile.fileName").value("file"));

    }

    @Test
    @DisplayName("계정 생성 정책을 준수하지 않으면 사용자 생성에 실패한다")
    void createUserFailWhenUserPolicyDoesNotSatisfy() throws Exception{
        //given
        UserCreateRequest invalidRequest = new UserCreateRequest(
                "test", //null, 빈문자열 미허용, 1자 이상 50자 이하
                "test",         // 이메일 형식 준수, 1자 이상 100자 이하
                "test",         //null, 빈문자열 미허용, 1자 이상 60자 이하
                null);

        MockMultipartFile createRequest = new MockMultipartFile("userCreateRequest", "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(invalidRequest).getBytes());

        //when & then
        mockMvc.perform(multipart("/api/users")
                        .file(createRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("ID로 유저 조회 성공")
    void findByIdSuccess() throws Exception{
        //given
        UUID uuid = UUID.randomUUID();
        User user = new User("test","test@test.com","test",null);
        ReflectionTestUtils.setField(user, "id", uuid);
        given(userService.findUserById(any(UUID.class))).willReturn(new UserResponse(user));

        //when & then
        mockMvc.perform(get("/api/users/{id}",uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"));

    }

    @Test
    @DisplayName("없는 사용자 조회 시 실패")
    void findByIdFailWhenUserNotExist() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        User user = new User("test","test@test.com","test",null);
        given(userService.findUserById(any(UUID.class))).willThrow(new UserException(UserErrorCode.USER_NOT_FOUND));

        //when & then
        mockMvc.perform(get("/api/users/{id}",uuid))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("사용자 전체 조회")
    void findAllUsers() throws Exception {
        //given
        User firstUser = new User("test","test@test.com","test",null);
        UUID firstUserId = UUID.randomUUID();

        User secondUser = new User("test2","test2@test.com","test2",null);
        UUID secondUserId = UUID.randomUUID();

        ReflectionTestUtils.setField(firstUser, "id", firstUserId);
        ReflectionTestUtils.setField(secondUser, "id", secondUserId);

        List<UserResponse> userResponses = Arrays.asList(new UserResponse(firstUser), new UserResponse(secondUser));
        given(userService.findUsers()).willReturn(userResponses);

        //when & then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(firstUserId.toString()))
                .andExpect(jsonPath("$[0].username").value("test"))
                .andExpect(jsonPath("$[0].email").value("test@test.com"))
                .andExpect(jsonPath("$[0].online").value(true))
                .andExpect(jsonPath("$[1].id").value(secondUserId.toString()))
                .andExpect(jsonPath("$[1].username").value("test2"))
                .andExpect(jsonPath("$[1].email").value("test2@test.com"))
                .andExpect(jsonPath("$[1].online").value(true));

    }

    @Test
    @DisplayName("사용자 업데이트를 성공한다")
    void updateUser() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        User user = new User("test","test@test.com","test",null);
        ReflectionTestUtils.setField(user, "id", uuid);
        UserUpdateRequest updateRequest = new UserUpdateRequest("update","update@update.com","update");

        User updatedUser = new User("update","update@update.com","update",null);
        ReflectionTestUtils.setField(updatedUser, "id", uuid);


        BinaryContent binaryContent = new BinaryContent("updatedProfile.jpg", "image/jpeg", 0L, "".getBytes());
        updatedUser.updateProfile(binaryContent);


        given(userService.updateUser(any(UserUpdateServiceRequest.class))).willReturn(new UserResponse(updatedUser));

        MockMultipartFile userUpdateRequestPart = new MockMultipartFile("userUpdateRequest", "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequest).getBytes());

        MockMultipartFile profilePart = new MockMultipartFile("profile", "updatedProfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "".getBytes());

        //when & then
        mockMvc.perform(multipart("/api/users/{id}",uuid)
                        .file(profilePart)
                        .file(userUpdateRequestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.username").value("update"))
                .andExpect(jsonPath("$.email").value("update@update.com"))
                .andExpect(jsonPath("$.profile.fileName").value("updatedProfile.jpg"));
    }

    @Test
    @DisplayName("없는 사용자 업데이트 시도 시 실패")
    void updateUserFailWhenUserNotExist() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        UserUpdateRequest updateRequest = new UserUpdateRequest("update","update@update.com","update");
        MockMultipartFile userUpdateRequestPart = new MockMultipartFile("userUpdateRequest", "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequest).getBytes());
        willThrow(new UserException(UserErrorCode.USER_NOT_FOUND)).given(userService).updateUser(any(UserUpdateServiceRequest.class));

        //when & then
        mockMvc.perform(multipart("/api/users/{id}",uuid)
                        .file(userUpdateRequestPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        }))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("사용자 삭제에 성공한다")
    void deleteUser() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        willDoNothing().given(userService).deleteUser(any(UUID.class));
        
        //when & then
        mockMvc.perform(delete("/api/users/{id}",uuid))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 사용자 삭제 시도 시 실패한다")
    void deleteUserFailWhenUserNotExist() throws Exception {
        //given
        UUID uuid = UUID.randomUUID();
        willThrow(new UserException(UserErrorCode.USER_NOT_FOUND)).given(userService).deleteUser(any(UUID.class));

        //when & then
        mockMvc.perform(delete("/api/users/{id}",uuid))
                .andExpect(status().isNotFound());
    }
}
