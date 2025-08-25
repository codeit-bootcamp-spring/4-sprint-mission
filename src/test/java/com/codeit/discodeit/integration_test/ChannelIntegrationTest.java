package com.codeit.discodeit.integration_test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.discodeit.auditing_config.AuditingConfig;
import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.channel_service_dto.PrivateChannelCreateRequest;
import com.codeit.discodeit.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserCreateRequest;
import com.codeit.discodeit.dto.user_service_dto.UserUpdateRequest;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

@Import(AuditingConfig.class)
class ChannelIntegrationTest extends IntegrationTestBasic {

  @Autowired
  private ChannelRepository channelRepository;

  private Channel defaultChannel;

  @BeforeEach
  void setUp() throws Exception {
    // 디폴트 공용 채널 생성
    channelRepository.deleteAll();
    defaultChannel = Channel.builder()
        .name("Default Channel")
        .description("Default Channel")
        .type(ChannelType.PUBLIC)
        .build();

    channelRepository.save(defaultChannel); // 저장 후 ID 자동 할당
  }

  @Test
  void 공용_채널_생성_성공_테스트() throws Exception {
    // given
    CreatePublicChannelRequestDto requestDto = new CreatePublicChannelRequestDto(
        "Public Channel",
        "This is a public channel"
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Public Channel"))
        .andExpect(jsonPath("$.description").value("This is a public channel"))
        .andExpect(jsonPath("$.id").isNotEmpty());
  }
  
  @Test
  void 필드_검증_실패시_공용_채널_생성_실패_테스트() throws Exception {
    // given
    CreatePublicChannelRequestDto requestDto = new CreatePublicChannelRequestDto("", "");

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/channels/public")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 사적_채널_생성_성공_테스트() throws Exception {
    // given
    List<UUID> participantIds = Arrays.asList(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest requestDto = new PrivateChannelCreateRequest(participantIds);

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty());
  }

  @Test
  void 필드_검증_실패시_사적_채널_생성_실패_테스트() throws Exception {
    // given
    PrivateChannelCreateRequest requestDto = new PrivateChannelCreateRequest(null);

    // when
    ResultActions resultActions = mockMvc.perform(
        post("/api/channels/private")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto))
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 공용_채널_업데이트_성공_테스트() throws Exception {
    // given
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "Updated Channel Name",
        "Updated Channel Description"
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/channels/{channelId}", defaultChannel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
    );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Channel Name"))
        .andExpect(jsonPath("$.description").value("Updated Channel Description"));
  }

  @Test
  void 필드_검증_실패시_공용_채널_업데이트_실패_테스트() throws Exception {
    // given
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "",
        ""
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/channels/{channelId}", defaultChannel.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
    );

    // then
    resultActions.andExpect(status().isBadRequest());
  }

  @Test
  void 채널이_없어서_공용_채널_업데이트_실패_테스트() throws Exception {
    // given
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest(
        "Updated Channel Name",
        "Updated Channel Description"
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/channels/{channelId}", UUID.randomUUID()) // // 완전히 없는 채널의 아이디를 가져옴
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  void 채널_삭제_성공_테스트() throws Exception {
    // given
    // 디폴트 채널 사용해서 딱히 given이 없음

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/channels/{channelId}", defaultChannel.getId())
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  void 채널이_없어서_채널_삭제_실패_테스트() throws Exception {
    // given
    UUID randomId = UUID.randomUUID();

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/channels/{channelId}", randomId)
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }
}