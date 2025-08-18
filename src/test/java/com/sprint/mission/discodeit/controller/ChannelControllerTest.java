package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.fixture.ChannelFixture;
import com.sprint.mission.discodeit.repository.fixture.UserFixture;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChannelController.class)
@Import({GlobalExceptionHandler.class})
public class ChannelControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private ChannelService channelService;
    @MockBean
    private ChannelMapper channelMapper;

    @DisplayName("공개 채널을 생성합니다. 공개 채널은 name과 description을 가질 수 있지만 선택 사항입니다.")
    @Test
    void createPublicChannel() throws Exception {
        // 본문 생성
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("name", "description");
        String content = om.writeValueAsString(request);

        // given
        Channel channel = Channel.of(UUID.randomUUID(), request.name(), request.description(), ChannelType.PUBLIC);
        given(channelService.createPublicChannel(request)).willReturn(channel);
        ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PUBLIC, "name", "description", null, Instant.now());
        given(channelMapper.toChannelDto(channel)).willReturn(channelDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content));

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()));
    }

    @DisplayName("비공개 채널을 생성합니다. 비공개 채널은 name과 description을 가지지 않습니다.")
    @Test
    void createPrivateChannel() throws Exception {
        // 본문 생성
        User hong = UserFixture.createUserHong();
        hong.setId(UUID.randomUUID());
        User kim = UserFixture.createUserKim();
        kim.setId(UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(hong.getId(), kim.getId()));
        String content = om.writeValueAsString(request);

        // given
        Channel channel = ChannelFixture.createPrivateChannel();
        given(channelService.createPrivateChannel(request)).willReturn(channel);
        UserDto hongDto = new UserDto(hong.getId(), hong.getUsername(), hong.getEmail(), null, true);
        UserDto kimDto = new UserDto(kim.getId(), kim.getUsername(), kim.getEmail(), null, true);
        List<UserDto> dtos = List.of(hongDto, kimDto);
        ChannelDto channelDto = new ChannelDto(channel.getId(), ChannelType.PRIVATE, null, null, dtos, Instant.now());
        given(channelMapper.toChannelDto(channel)).willReturn(channelDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
                .andExpect(jsonPath("$.participants").isNotEmpty());
    }

    @DisplayName("비공개 채널을 만들기 위해 유저는 최소 2명 이상이 필요합니다.")
    @Test
    void createPrivateChannelShouldFailedWhenUserLessThanTwo() throws Exception {
        // 본문 생성
        User hong = UserFixture.createUserHong();
        hong.setId(UUID.randomUUID());
        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(List.of(hong.getId()));
        String content = om.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].reason").value("최소 2명 이상이 대화에 참여해야 합니다."));
    }
}
