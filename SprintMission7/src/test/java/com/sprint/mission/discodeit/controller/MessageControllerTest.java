package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
class MessageControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private MessageService messageService;

  @Test
  @DisplayName("POST /api/messages - 성공 케이스")
  void createMessage_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Instant now = Instant.now();

    UserDto author = new UserDto(authorId, "testuser", "nickname", null, null);

    MessageDto dto =
        new MessageDto(messageId, now, now, "Hello World", channelId, author, List.of());

    MessageCreateRequest request = new MessageCreateRequest("Hello World", channelId, authorId);

    MockMultipartFile messagePart =
        new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request));

    BDDMockito.given(messageService.create(any(MessageCreateRequest.class), any())).willReturn(dto);

    mockMvc
        .perform(
            multipart("/api/messages")
                .file(messagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("Hello World"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.author.id").value(authorId.toString()));
  }

  @Test
  @DisplayName("POST /api/messages - 실패 케이스 (빈 내용)")
  void createMessage_fail_emptyContent() throws Exception {
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();

    MessageCreateRequest request = new MessageCreateRequest("", channelId, authorId);

    MockMultipartFile messagePart =
        new MockMultipartFile(
            "messageCreateRequest",
            "",
            "application/json",
            objectMapper.writeValueAsBytes(request));

    mockMvc
        .perform(
            multipart("/api/messages")
                .file(messagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("PATCH /api/messages/{messageId} - 성공 케이스")
  void updateMessage_success() throws Exception {
    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    Instant now = Instant.now();

    UserDto author = new UserDto(authorId, "testuser", "nickname", null, null);

    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");

    MessageDto dto =
        new MessageDto(messageId, now, now, "Updated Content", channelId, author, List.of());

    BDDMockito.given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willReturn(dto);

    mockMvc
        .perform(
            patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(messageId.toString()))
        .andExpect(jsonPath("$.content").value("Updated Content"));
  }

  @Test
  @DisplayName("PATCH /api/messages/{messageId} - 실패 케이스 (존재하지 않는 메시지)")
  void updateMessage_fail_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();
    MessageUpdateRequest request = new MessageUpdateRequest("Updated Content");

    BDDMockito.given(messageService.update(eq(messageId), any(MessageUpdateRequest.class)))
        .willThrow(new RuntimeException("Message not found"));

    mockMvc
        .perform(
            patch("/api/messages/{messageId}", messageId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError()); // 서비스 예외 → 500
  }

  @Test
  @DisplayName("DELETE /api/messages/{messageId} - 성공 케이스")
  void deleteMessage_success() throws Exception {
    UUID messageId = UUID.randomUUID();

    BDDMockito.willDoNothing().given(messageService).delete(messageId);

    mockMvc
        .perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/messages/{messageId} - 실패 케이스 (존재하지 않는 메시지)")
  void deleteMessage_fail_notFound() throws Exception {
    UUID messageId = UUID.randomUUID();

    BDDMockito.willThrow(new RuntimeException("Message not found"))
        .given(messageService)
        .delete(messageId);

    mockMvc
        .perform(delete("/api/messages/{messageId}", messageId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("GET /api/messages?channelId= - 성공 케이스")
  void findAllMessages_success() throws Exception {
    UUID channelId = UUID.randomUUID();
    Instant now = Instant.now();
    MessageDto msg1 =
        new MessageDto(
            UUID.randomUUID(),
            now,
            now,
            "msg1",
            channelId,
            new UserDto(UUID.randomUUID(), "u1", "nick1", null, null),
            List.of());
    MessageDto msg2 =
        new MessageDto(
            UUID.randomUUID(),
            now,
            now,
            "msg2",
            channelId,
            new UserDto(UUID.randomUUID(), "u2", "nick2", null, null),
            List.of());

    PageResponse<MessageDto> pageResponse =
        new PageResponse<>(
            List.of(msg1, msg2),
            null, // nextCursor
            2, // size
            true, // hasNext
            2L // totalElements
            );

    BDDMockito.given(messageService.findAllByChannelId(eq(channelId), any(), any()))
        .willReturn(pageResponse);

    mockMvc
        .perform(
            get("/api/messages")
                .param("channelId", channelId.toString())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].content").value("msg1"))
        .andExpect(jsonPath("$.content[1].content").value("msg2"))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  @Test
  @DisplayName("GET /api/messages?channelId= - 실패 케이스 (잘못된 UUID)")
  void findAllMessages_fail_invalidChannelId() throws Exception {
    String invalidId = "not-a-uuid";

    mockMvc
        .perform(
            get("/api/messages").param("channelId", invalidId).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }
}
