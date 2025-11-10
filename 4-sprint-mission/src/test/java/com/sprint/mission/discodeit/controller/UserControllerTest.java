package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({GlobalExceptionHandler.class})
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private BinaryContentMapper binaryContentMapper;
    @MockitoBean
    private BinaryContentService binaryContentService;

    @DisplayName("유저를 생성할 때 profile은 선택사항입니다. profile이 존재하지 않아도 정상적으로 유저를 생성합니다.")
    @Test
    void createUser() throws Exception {
        // 본문 생성
        UserCreateRequest request = new UserCreateRequest("username", "password", "test@email.com");
        String content = om.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile("userCreateRequest", "userCreateRequest", MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        // given
        User user = new User(request, null);
        given(userService.createUser(request, null)).willReturn(user);
        UserDto userDto = new UserDto(UUID.randomUUID(), "username", "test@email.com", null, true, Role.USER);
        given(userMapper.toDto(user)).willReturn(userDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        resultActions.andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.online").value(true))
                .andExpect(status().isCreated());
    }

    @DisplayName("profile이 포함된 경우 BinaryContent를 생성하여 유저기 추가 연관관계를 가집니다.")
    @Test
    void createUserWithProfile() throws Exception {
        // 본문 생성
        UserCreateRequest request = new UserCreateRequest("username", "password", "test@email.com");
        MockMultipartFile profile = new MockMultipartFile("profile", "profileImg", MediaType.IMAGE_JPEG_VALUE, "test".getBytes());
        String content = om.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile("userCreateRequest", "userCreateRequest", MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        // given
        User user = new User(request, null);
        BinaryContent binaryContent = BinaryContent.of(UUID.randomUUID(), profile);
        given(userService.createUser(request, binaryContent.getId())).willReturn(user);
        BinaryContentDto binaryContentDto = new BinaryContentDto(binaryContent.getId(), binaryContent.getSize(), binaryContent.getFileName(), binaryContent.getContentType());
        UserDto userDto = new UserDto(UUID.randomUUID(), "username", "test@email.com", binaryContentDto, true, Role.USER);
        given(userMapper.toDto(user)).willReturn(userDto);
        given(binaryContentService.create(profile)).willReturn(binaryContent);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .file(profile)
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.online").value(true));
    }

    @DisplayName("DTO 검증 통과 실패. username은 필수 항목입니다. null이나 공백이 들어갈 수 없습니다.")
    @Test
    void createUserShouldFailedWhenUsernameIsNull() throws Exception {
        // 본문 생성
        UserCreateRequest request = new UserCreateRequest("", "password", "test@email.com");
        String content = om.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .param("userCreateRequest", content)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest());
    }
}
