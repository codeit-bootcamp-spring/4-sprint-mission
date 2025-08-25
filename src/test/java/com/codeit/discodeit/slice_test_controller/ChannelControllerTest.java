package com.codeit.discodeit.slice_test_controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.discodeit.controller.ChannelController;
import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.channel_service_dto.PrivateChannelCreateRequest;
import com.codeit.discodeit.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit.exception.channel.ChannelNameDuplicateException;
import com.codeit.discodeit.exception.global.GlobalExceptionHandler;
import com.codeit.discodeit.service.ChannelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = ChannelController.class)
@Import({GlobalExceptionHandler.class, ChannelConfig.class})
@ActiveProfiles("test")
class ChannelControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void 공용_채널_생성_API_요청_성공_테스트() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();

    CreatePublicChannelRequestDto requestDto = new CreatePublicChannelRequestDto(
        "test Channel",
        "This is a public test channel"
    );

    ChannelDto mockChannelDto = new ChannelDto(channelId, null, requestDto.getName(), requestDto.getDescription(), null, null);

    // 서비스 호출 모킹
    given(channelService.createPublicChannel(any(CreatePublicChannelRequestDto.class)))
        .willReturn(mockChannelDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                        {
                          "name": "My Channel",
                          "description": "This is a public channel"
                        }
                        """));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(channelId.toString()))
        .andExpect(jsonPath("$.name").value("test Channel"))
        .andExpect(jsonPath("$.description").value("This is a public test channel"));
    
  }

  @Test
  void 공용_채널_생성_API_요청_실패_테스트() throws Exception {
    // given
    given(channelService.createPublicChannel(any(CreatePublicChannelRequestDto.class)))
        .willThrow(new ChannelNameDuplicateException(null));

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/channels/public")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
                    {
                      "name": "test Channel",
                      "description": "This is a public test channel"
                    }
                    """));

    // then: ErrorResponse 검증
    resultActions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.exceptionType").value("ChannelNameDuplicateException"));
  }

  @Test
  void 사적_채널_생성_API_요청_성공_테스트() throws Exception {
    // given
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    PrivateChannelCreateRequest request = new PrivateChannelCreateRequest(participantIds);
    String requestJson = objectMapper.writeValueAsString(request);

    ChannelDto mockChannelDto = new ChannelDto(UUID.randomUUID(), null, null, null, null, null);

    given(channelService.createPrivateChannel(participantIds))
        .willReturn(mockChannelDto);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestJson));

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(mockChannelDto.getId().toString()));
  }

  @Test
  void 참여_유저가_없어서_사적_채널_생성_API_요청_실패_테스트() throws Exception {
    // given
    PrivateChannelCreateRequest invalidRequest = new PrivateChannelCreateRequest(null);

    // when
    ResultActions resultActions = mockMvc.perform(post("/api/channels/private")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)));

    // then
    resultActions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FAILED_VALIDATION_ERROR"));
  }

  @Test
  void 공용_채널_업데이트_API_요청_성공_테스트() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    PublicChannelUpdateRequest updateRequest = new PublicChannelUpdateRequest("newName", "newDescription");

    ChannelDto updatedChannelDto = new ChannelDto(channelId, null, updateRequest.getNewName(), updateRequest.getNewDescription(), null, null);

    given(channelService.updatePublicChannel(
        eq(channelId), any(PublicChannelUpdateRequest.class)))
        .willReturn(updatedChannelDto);

    // when
    ResultActions resultActions = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updateRequest)));

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedChannelDto.getId().toString()));
  }

  @Test
  void 공용_채널_업데이트_API_요청_검증_실패_테스트() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    // newName이 empty이면 @NotEmpty 검증 실패
    PublicChannelUpdateRequest invalidRequest = new PublicChannelUpdateRequest("", "");

    // when
    ResultActions resultActions = mockMvc.perform(patch("/api/channels/{channelId}", channelId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidRequest)));

    // then
    resultActions.andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("FAILED_VALIDATION_ERROR"));
  }
}
// TODO Auditing 문제가 생김
// 추후에 메인에는 따로 적용할 수 있도록 해야할듯