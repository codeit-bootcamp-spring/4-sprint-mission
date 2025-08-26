package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ChannelController.class)
class ChannelControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper om;

  @MockitoBean private ChannelService channelService;

  @Test
  @DisplayName("POST /api/channels/public - 성공 케이스")
  void createPublicChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    ChannelDto dto =
        new ChannelDto(channelId, ChannelType.PUBLIC, "public-channel", "테스트 설명", List.of(), null);

    PublicChannelCreateRequest request = new PublicChannelCreateRequest("public-channel", "테스트 설명");

    BDDMockito.given(channelService.create(any(PublicChannelCreateRequest.class))).willReturn(dto);

    mockMvc
        .perform(
            post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.type").value("PUBLIC"))
        .andExpect(jsonPath("$.name").value("public-channel"))
        .andExpect(jsonPath("$.description").value("테스트 설명"));
  }

  @Test
  @DisplayName("POST /api/channels/public - 실패 케이스 (빈 이름)")
  void createPublicChannel_fail_emptyName() throws Exception {
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("", "설명 없음");

    mockMvc
        .perform(
            post("/api/channels/public")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/channels/{channelId} - 성공 케이스")
  void updateChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request =
        new PublicChannelUpdateRequest("updated-name", "updated-desc");

    ChannelDto dto =
        new ChannelDto(
            channelId, ChannelType.PUBLIC, "updated-name", "updated-desc", List.of(), null);

    BDDMockito.given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willReturn(dto);

    mockMvc
        .perform(
            patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("updated-name"))
        .andExpect(jsonPath("$.description").value("updated-desc"));
  }

  @Test
  @DisplayName("PATCH /api/channels/{channelId} - 실패 케이스 (존재하지 않는 채널)")
  void updateChannel_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest request = new PublicChannelUpdateRequest("name", "desc");

    BDDMockito.given(channelService.update(eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willThrow(new RuntimeException("Channel not found")); // 실제로는 NotFoundException 정의 가능

    mockMvc
        .perform(
            patch("/api/channels/{channelId}", channelId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("DELETE /api/channels/{channelId} - 성공 케이스")
  void deleteChannel_success() throws Exception {
    UUID channelId = UUID.randomUUID();

    BDDMockito.willDoNothing().given(channelService).delete(channelId);

    mockMvc
        .perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/channels/{channelId} - 실패 케이스 (존재하지 않는 채널)")
  void deleteChannel_fail_notFound() throws Exception {
    UUID channelId = UUID.randomUUID();
    BDDMockito.willThrow(new RuntimeException("Channel not found"))
        .given(channelService)
        .delete(channelId);

    mockMvc
        .perform(delete("/api/channels/{channelId}", channelId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("GET /api/channels?userId= - 성공 케이스")
  void findAllChannels_success() throws Exception {
    UUID userId = UUID.randomUUID();
    ChannelDto channel1 =
        new ChannelDto(UUID.randomUUID(), ChannelType.PUBLIC, "channel1", "설명1", List.of(), null);
    ChannelDto channel2 =
        new ChannelDto(UUID.randomUUID(), ChannelType.PRIVATE, "channel2", "설명2", List.of(), null);

    BDDMockito.given(channelService.findAllByUserId(userId))
        .willReturn(List.of(channel1, channel2));

    mockMvc
        .perform(
            get("/api/channels")
                .param("userId", userId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("channel1"))
        .andExpect(jsonPath("$[1].name").value("channel2"));
  }

  @Test
  @DisplayName("GET /api/channels?userId= - 실패 케이스 (잘못된 UUID)")
  void findAllChannels_fail_invalidUserId() throws Exception {
    String invalidUserId = "not-a-uuid";

    mockMvc
        .perform(
            get("/api/channels").param("userId", invalidUserId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
