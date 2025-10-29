package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class UserIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("사용자 생성 통합 테스트")
    void createUser() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());

        //when
        ResultActions resultActions = createUser(userPart, profilePart);

        //when & then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("test"))
                .andExpect(jsonPath("$.email").value("test@test.com"))
                .andExpect(jsonPath("$.profile.fileName").value("test-profile.jpg"))
                .andExpect(jsonPath("$.profile.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.profile.size").value(0))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("사용자 업데이트 통합 테스트")
    void updateUser() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());

        ResultActions resultActions = createUser(userPart, profilePart);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest("update","update@update.com","update");
        MockMultipartFile userUpdatePart = new MockMultipartFile("userUpdateRequest", "","application/json", objectMapper.writeValueAsString(userUpdateRequest).getBytes());
        MockMultipartFile profileUpdatePart = new MockMultipartFile("profile", "update-profile.jpg", "image/jpeg", "".getBytes());

        // 응답 본문을 변환 (예: UserResponse.class)
        String json = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println(json);

        UserResponse userResponse = objectMapper.readValue(json, UserResponse.class);
        UUID userId = userResponse.getId();

        //when & then
        mockMvc.perform(multipart("/api/users/{userId}",userId)
                .file(userUpdatePart)
                .file(profileUpdatePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(request -> {
                    request.setMethod("PATCH");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("update"))
                .andExpect(jsonPath("$.email").value("update@update.com"))
                .andExpect(jsonPath("$.profile.fileName").value("update-profile.jpg"))
                .andExpect(jsonPath("$.profile.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.profile.size").value(0))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    @DisplayName("사용자 삭제 통합테스트")
    void deleteUser() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test", "test@test.com", "test", null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg",
                "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "", "application/json",
                objectMapper.writeValueAsString(userCreateRequest).getBytes());

        ResultActions resultActions = createUser(userPart, profilePart);

        UserResponse userResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(),
                UserResponse.class);
        UUID userId = userResponse.getId();

        //when & then
        mockMvc.perform(delete("/api/users/{userId}", userId)
                        )
                .andExpect(status().isNoContent());
    }

    private ResultActions createUser(MockMultipartFile userPart, MockMultipartFile profilePart) throws Exception {
        return mockMvc.perform(multipart("/api/users")
                .file(userPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA));
    }

}
