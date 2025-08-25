package com.codeit.discodeit.slice_test_controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.discodeit.controller.ChannelController;
import com.codeit.discodeit.controller.MessageController;
import com.codeit.discodeit.dto.channel_service_dto.ChannelDto;
import com.codeit.discodeit.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit.dto.channel_service_dto.PrivateChannelCreateRequest;
import com.codeit.discodeit.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit.dto.message_service_dto.MessageDto;
import com.codeit.discodeit.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit.exception.channel.ChannelNameDuplicateException;
import com.codeit.discodeit.exception.global.GlobalExceptionHandler;
import com.codeit.discodeit.service.ChannelService;
import com.codeit.discodeit.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(controllers = MessageController.class)
@Import({GlobalExceptionHandler.class, MessageConfig.class})
@ActiveProfiles("test")
class MessageControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private MessageService messageService;

  @Autowired
  private ObjectMapper objectMapper = new ObjectMapper();

  @Test
  void 메세지_생성_API_요청_성공_테스트() throws Exception {
    // given
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest requestDto = new MessageCreateRequest(
        "Hello World",
        channelId,
        authorId
    );

    String requestJson = objectMapper.writeValueAsString(requestDto);

    MockMultipartFile messagePart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        MediaType.APPLICATION_JSON_VALUE,
        requestJson.getBytes()
    );

    MockMultipartFile attachment = new MockMultipartFile(
        "attachments",
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "file content".getBytes()
    );

    MessageDto mockResponse = new MessageDto(
        UUID.randomUUID(), null, null, null, channelId, null, null);

    given(messageService.createMessage(any(), any()))
        .willReturn(mockResponse);

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/messages")
            .file(messagePart)
            .file(attachment)
            .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(mockResponse.id().toString()));
  }

  @Test
  void 메시지_삭제_API_요청_성공_테스트() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    willDoNothing().given(messageService).deleteMessage(messageId);

    // when
    ResultActions result = mockMvc.perform(delete("/api/messages/{messageId}", messageId));

    // then
    result.andExpect(status().isNoContent());
  }

  @Test
  void 메시지_수정_API_요청_성공_테스트() throws Exception {
    // given
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("수정된 내용");

    MessageDto updatedMessage = new MessageDto(messageId, null, null, updateRequest.getNewContent(), null, null, null);

    given(messageService.updateMessage(eq(messageId), any(MessageUpdateRequest.class)))
        .willReturn(updatedMessage);

    // when
    ResultActions result = mockMvc.perform(
        patch("/api/messages/{messageId}", messageId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(updateRequest))
    );

    // then
    result.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("수정된 내용"));

  }
}
// TODO Auditing 문제가 생김
// 추후에 메인에는 따로 적용할 수 있도록 해야할듯