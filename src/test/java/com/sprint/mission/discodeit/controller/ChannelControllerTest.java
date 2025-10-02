package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.constant.ChannelErrorCode;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateServiceRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;

    @Test
    @DisplayName("공개 채널 생성가 성공한다")
    void createPublicChannel_Success() throws Exception {
        //given
        ChannelCreateRequest request = new ChannelCreateRequest("공개 채널", "공개 채널");

        UUID channelId = UUID.randomUUID();
        ChannelResponse response = new ChannelResponse(channelId, ChannelType.PUBLIC, "공개 채널", "공개 채널",
                new ArrayList<>(), Instant.now());

        given(channelService.createPublicChannel(any(ChannelCreateServiceRequest.class))).willReturn(response);

        //when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.name").value("공개 채널"))
                .andExpect(jsonPath("$.description").value("공개 채널"));

    }

    @Test
    @DisplayName("입력값 제약조건 미준수 시 공개 채널 생성이 실패한다")
    void createPublicChannel_Fail() throws Exception {
        //given
        ChannelCreateRequest request = new ChannelCreateRequest(
                "",                 // 1자 ~ 100자 이어야 함
                "A".repeat(501)     // 1자 ~ 500자 이어야함
        );

        //when & then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("비공개 채널 생성을 성공한다")
    void createPrivateChannel_Success() throws Exception {
        //given
        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();
        UUID thirdId = UUID.randomUUID();

        List<UUID> participantIds = List.of(firstId, secondId, thirdId);
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);

        List<UserResponse> users = new ArrayList<>();
        for (UUID participantId : participantIds) {
            User user = new User("user", "user@user.com", "user", null);
            ReflectionTestUtils.setField(user, "id", participantId);
            users.add(new UserResponse(user));
        }

        ChannelResponse response = new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, "비공개 채널", "비공개 채널",
                users, Instant.now());

        given(channelService.createPrivateChannel(any(PrivateChannelCreateServiceRequest.class))).willReturn(response);

        //when & then
        mockMvc.perform(post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId().toString()))
                .andExpect(jsonPath("$.name").value("비공개 채널"))
                .andExpect(jsonPath("$.description").value("비공개 채널"));
    }

    @Test
    @DisplayName("공개 채널 업데이트를 성공한다")
    void updatePublicChannel_Success() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        ChannelUpdateRequest request = new ChannelUpdateRequest("공개 채널 수정", "공개 채널 수정");

        ChannelResponse response = new ChannelResponse(id, ChannelType.PUBLIC, "공개 채널 수정", "공개 채널 수정", null,
                Instant.now());

        given(channelService.updateChannel(any(ChannelUpdateServiceRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("공개 채널 수정"))
                .andExpect(jsonPath("$.description").value("공개 채널 수정"));
    }

    @Test
    @DisplayName("비공개 채널 업데이트를 실패한다")
    void updatePrivateChannel_Fail() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        ChannelUpdateRequest request = new ChannelUpdateRequest("비공개 채널 수정", "비공개 채널 수정");

        given(channelService.updateChannel(any(ChannelUpdateServiceRequest.class))).willThrow(new ChannelException(
                ChannelErrorCode.PRIVATE_CHANNEL_NOT_EDITABLE));

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("없는 채널 업데이트 시도 시 실패한다")
    void updateNotExistChannel_Fail() throws Exception {
        //given
        UUID id = UUID.randomUUID();
        ChannelUpdateRequest request = new ChannelUpdateRequest("공개 채널 수정", "공개 채널 수정");

        given(channelService.updateChannel(any(ChannelUpdateServiceRequest.class))).willThrow(new ChannelException(
                ChannelErrorCode.CHANNEL_NOT_FOUND));

        // when & then
        mockMvc.perform(patch("/api/channels/{channelId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 삭제를 성공한다")
    void deleteChannel_Success() throws Exception {
        //given
        UUID channelId = UUID.randomUUID();
        willDoNothing().given(channelService).deleteChannel(any(UUID.class));

        //when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("없는 채널 삭제 시도 시 실패한다")
    void deleteChannelFailIfChannelNotExist() throws Exception {
        //given
        UUID channelId = UUID.randomUUID();
        willThrow(new ChannelException(ChannelErrorCode.CHANNEL_NOT_FOUND)).given(channelService)
                .deleteChannel(any(UUID.class));

        //when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("사용자는 자신이 참여한 채널만 조회 가능하다")
    void findAllByUserId_Success() throws Exception {
        //given
        UUID firstUserId = UUID.randomUUID();
        UUID secondUserId = UUID.randomUUID();

        User firstUser = new User("test", "test@test.com", "test", null);
        ReflectionTestUtils.setField(firstUser, "id", firstUserId);

        User secondUser = new User("test", "test@test.com", "test", null);
        ReflectionTestUtils.setField(secondUser, "id", secondUserId);
        List<UserResponse> users = List.of(new UserResponse(firstUser), new UserResponse(secondUser));

        List<ChannelResponse> channels = new ArrayList<>();
        channels.add(
                new ChannelResponse(UUID.randomUUID(), ChannelType.PUBLIC, "공개 채널", "공개 채널", List.of(), Instant.now()));
        channels.add(
                new ChannelResponse(UUID.randomUUID(), ChannelType.PRIVATE, "비공개 채널", "비공개 채널", users, Instant.now()));

        given(channelService.findAllChannelsByUserId(any(UUID.class))).willReturn(channels);

        //when & then
        mockMvc.perform(get("/api/channels?userId={userId}", firstUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(channels.get(0).getId().toString()))
                .andExpect(jsonPath("$[0].name").value(channels.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(channels.get(0).getDescription()))
                .andExpect(jsonPath("$[1].id").value(channels.get(1).getId().toString()))
                .andExpect(jsonPath("$[1].name").value(channels.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(channels.get(1).getDescription()));
    }
}
