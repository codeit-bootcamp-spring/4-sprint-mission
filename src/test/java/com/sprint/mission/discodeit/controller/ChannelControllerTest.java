package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ChannelController.class)
@Import({GlobalExceptionHandler.class})
class ChannelControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성 성공 - 201 Created, 채널 생성 정보 반환")
    void createChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("name", "description");
        ChannelDto channelDto = new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                request.name(),
                request.description(),
                List.of(mock(UserDto.class), mock(UserDto.class)),
                Instant.now()
        );

        given(channelService.create(any(PublicChannelCreateRequest.class)))
                .willReturn(channelDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    @DisplayName("채널 생성 실패 - 유효성 검증 실패(채널명 공백), 400 반환")
    void createPublicChannelFailCausedByChannelNameIsBlank() throws Exception {
        // given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(" ", "description");

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("개인 채널 생성 성공 - 201 Created, 채널 정보 반환")
    void createPrivateChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId1, userId2));

        UserDto userDto1 = new UserDto(userId1, "username1", "email1@codeit.com", null, true);
        UserDto userDto2 = new UserDto(userId2, "username2", "email2@codeit.com", null, false);

        ChannelDto channelDto = new ChannelDto(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                List.of(userDto1, userDto2),
                Instant.now()
        );
        given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willReturn(channelDto);

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(channelId.toString()));
    }

    @Test
    @DisplayName("개인 채널 생성 실패 - Content-Type 잘못, 500 반환")
    void createPrivateChannelFailCausedByWrongContentType() throws Exception {
        // given
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(userId1, userId2));

        // when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().is5xxServerError());
    }

    @Test
    @DisplayName("채널 업데이트 성공 - 200 OK, 채널 업데이트 정보 반환")
    void updateChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");
        ChannelDto channelDto = new ChannelDto(
                channelId,
                ChannelType.PUBLIC,
                request.newName(),
                request.newDescription(),
                List.of(mock(UserDto.class), mock(UserDto.class))
                , Instant.now()
        );
        given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
                .willReturn(channelDto);

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("newName"))
                .andExpect(jsonPath("$.description").value("newDescription"));
    }

    @Test
    @DisplayName("채널 업데이트 실패 - 잘못된 파라미터(channelId)로 채널 찾을 수 없음")
    void updateChannelFailCausedByWrongChannelId() throws Exception {
        // given
        UUID wrongChannelId = UUID.randomUUID();
        PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("newName", "newDescription");

        given(channelService.update(eq(wrongChannelId), any(PublicChannelUpdateRequest.class)))
                .willThrow(new ChannelNotFoundException(Map.of("channelId", wrongChannelId.toString())));

        // when
        ResultActions actions = mockMvc.perform(
                patch("/api/channels/{channelId}", wrongChannelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON)
        );

        // then
        actions.andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 삭제 성공 - 204 NoContent")
    void deleteChannelSuccess() throws Exception {
        // given
        UUID channelId = UUID.randomUUID();

        // when
        ResultActions actions = mockMvc.perform(
                delete("/api/channels/{channelId}", channelId)
        );

        // then
        actions.andExpect(status().isNoContent());
    }
}