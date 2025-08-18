package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.InsufficientParticipantsException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.service.ChannelService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@Import(GlobalExceptionHandler.class)
public class ChannelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private ChannelService channelService;
    @MockitoBean
    private ChannelMapper channelMapper;

    @Test
    void 공개_채널_생성() throws Exception {
        //given
        PublicChannelCreateRequest request = new PublicChannelCreateRequest("공개방", "모든 유저가 참여 가능");
        ChannelDto responseDto = new ChannelDto(
                UUID.randomUUID(),
                ChannelType.PUBLIC,
                "공개방",
                "모든 유저가 사용 가능",
                List.of(),
                null
        );

        given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(responseDto);

        //when + then
        mockMvc.perform(post("/api/channels/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.id().toString()))
                .andExpect(jsonPath("$.type").value("PUBLIC"))
                .andExpect(jsonPath("$.name").value("공개방"))
                .andExpect(jsonPath("$.description").value("모든 유저가 사용 가능"))
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.lastMessageAt").doesNotExist());
    }

    @Test
    void 비공개_채널_생성_성공() throws Exception {
        //given
        UUID kimId = UUID.randomUUID();
        UUID jimId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();

        List<UUID> participantIds = Arrays.asList(kimId, jimId);

        List<UserDto> participants = Arrays.asList(
                new UserDto(kimId, "kim", "kim@nate.com", null, false),
                new UserDto(jimId, "jim", "jim@nate.com", null, false)
        );

        ChannelDto savedChannel = new ChannelDto(
                channelId,
                ChannelType.PRIVATE,
                null,
                null,
                participants,
                null
        );

        given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willReturn(savedChannel);

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
        String body = objectMapper.writeValueAsString(request);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(body)
        );

        //then
        actions.andDo(print())  // 디버깅용 출력
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value("PRIVATE"))
                .andExpect(jsonPath("$.name").doesNotExist())
                .andExpect(jsonPath("$.description").doesNotExist())
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants", hasSize(2)))
                .andExpect(jsonPath("$.participants[0].id").value(kimId.toString()))
                .andExpect(jsonPath("$.participants[0].username").value("kim"))
                .andExpect(jsonPath("$.participants[0].email").value("kim@nate.com"))
                .andExpect(jsonPath("$.participants[1].id").value(jimId.toString()))
                .andExpect(jsonPath("$.participants[1].username").value("jim"))
                .andExpect(jsonPath("$.participants[1].email").value("jim@nate.com"))
                .andExpect(jsonPath("$.lastMessageAt").doesNotExist());
    }

    @Test
    void 비공개_채널_생성_실패() throws Exception { //참가자가 2명 미만일 경우
        //given
        UUID kimId = UUID.randomUUID();
        List<UUID> insufficientParticipants = Collections.singletonList(kimId);

        given(channelService.create(any(PrivateChannelCreateRequest.class)))
                .willThrow(new InsufficientParticipantsException("비공개 채널은 최소 2명의 참가자가 필요합니다"));

        PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(insufficientParticipants);
        String content = objectMapper.writeValueAsString(request);

        //when
        ResultActions actions = mockMvc.perform(
                post("/api/channels/private")
                        .contentType(MediaType.APPLICATION_JSON)  // 서버가 처리할 타입
                        .accept(MediaType.APPLICATION_JSON)       // 응답 기대 타입
                        .content(content)                            // JSON 본문
        );

        //then
        actions.andDo(print())  // 디버깅용 출력
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("비공개 채널은 최소 2명의 참가자가 필요합니다"));
    }
}
