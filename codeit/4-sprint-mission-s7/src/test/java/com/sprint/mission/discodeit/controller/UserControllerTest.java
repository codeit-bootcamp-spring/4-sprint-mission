package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;


@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserStatusService userStatusService;
    @MockitoBean
    private BinaryContentService binaryContentService;
    @MockitoBean
    private UserMapper userMapper;
    @MockitoBean
    private UserStatusMapper userStatusMapper;
    @MockitoBean
    private BinaryContentMapper binaryContentMapper;

    @Test
    void 프로필없이_유저_생성() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest("kim", "kim@nate.com", "kim1234!");
        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile("userCreateRequest", "userCreateRequest", MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        UUID userId = UUID.randomUUID();
        UserDto user = new UserDto(userId, "kim", "kim@nate.com", null, true);

        given(userService.create(eq(request),eq(Optional.empty()))).willReturn(user);

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("kim"))
                .andExpect(jsonPath("$.email").value("kim@nate.com"))
                .andExpect(jsonPath("$.profileUrl").doesNotExist())
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    void 프로필을_포함한_유저_생성() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest("kim", "kim@nate.com", "kim1234!");
        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest", "userCreateRequest",
                MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        MockMultipartFile profileFile = new MockMultipartFile(
                "profile", "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE, "profile image content".getBytes());

        UUID userId = UUID.randomUUID();
        BinaryContentDto profileDto = new BinaryContentDto(UUID.randomUUID(), "profile.jpg", 1024L, "image/jpeg");
        UserDto userDto = new UserDto(userId, "kim", "kim@nate.com", profileDto, true);

        given(userService.create(eq(request),any(Optional.class))).willReturn(userDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .file(userCreateRequest)
                        .file(profileFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("kim"))
                .andExpect(jsonPath("$.email").value("kim@nate.com"))
                .andExpect(jsonPath("$.profile.fileName").value("profile.jpg"))
                .andExpect(jsonPath("$.profile.size").value(1024))
                .andExpect(jsonPath("$.profile.contentType").value("image/jpeg"))
                .andExpect(jsonPath("$.online").value(true));
    }

    @Test
    void 이메일이_존재하지_않아서_유저_생성_실패() throws Exception {
        //given
        UserCreateRequest request = new UserCreateRequest("kim", null, "kim1234!");
        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile userCreateRequest = new MockMultipartFile(
                "userCreateRequest", "userCreateRequest",
                MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        UUID userId = UUID.randomUUID();
        BinaryContentDto profileDto = new BinaryContentDto(UUID.randomUUID(), "profile.jpg", 1024L, "image/jpeg");
        UserDto userDto = new UserDto(userId, "kim", "kim@nate.com", profileDto, true);

        given(userService.create(any(UserCreateRequest.class), any(Optional.class))).willReturn(userDto);

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/users")
                        .file(userCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.details.email").exists());
    }

}
