package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  MessageService messageService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * POST - create
   *
   * @throws Exception
   */
  @DisplayName("메세지 생성 테스트")
  @Test
  void postMessage_returnsMessageDto() throws Exception {
    // ================== given ==================

    // ====== The DTOs we will send
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        "Hello!",
        channelId,
        authorId
    );
    String messageCreateRequestString = objectMapper.writeValueAsString(messageCreateRequest);

    // 💡 @RequestPart("messageCreateRequest")
    // the endpoint only accepts requests (formatted as multipart/form-data)
    MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
        "messageCreateRequest", // must match the @RequestPart name in the controller
        "",
        MediaType.APPLICATION_JSON_VALUE,
        messageCreateRequestString.getBytes()
    );

    // 💡 @RequestPart(value = "attachments", required = false)
    // for the controller
    MockMultipartFile image1 = new MockMultipartFile(
        "attachments", // matching the @RequestPart name
        "image1.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // ====== The DTO that the mocked service will return
    MessageDto mockResponseDto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        "Hello!",
        channelId,
        mock(UserDto.class),
        List.of(
            new BinaryContentDto(
                UUID.randomUUID(),
                "image1",
                (long) "test-image-data".getBytes().length,
                MediaType.IMAGE_PNG_VALUE
            )
        )
    );

    when(messageService.create(any(MessageCreateRequest.class), anyList()))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(multipart("/api/messages") // multipart builder
            .file(messageCreateRequestPart)
            .file(image1))
//        .file(image2) // you can just add more here
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.content").value("Hello!"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()))
        .andExpect(jsonPath("$.attachments", hasSize(1)))
        .andExpect(jsonPath("$.attachments[0].fileName").value("image1"))
        .andExpect(jsonPath("$.attachments[0].contentType").value(MediaType.IMAGE_PNG_VALUE));
  }

  @DisplayName("메세지 생성 실패 - Validation error")
  @Test
  void postMessage_validationError_Failure() throws Exception {
    // ================== given ==================

    // ====== The DTOs we will send
    UUID channelId = UUID.randomUUID();
    UUID authorId = UUID.randomUUID();
    MessageCreateRequest messageCreateRequest = new MessageCreateRequest(
        null, // << VALIDATION ERROR HERE
        channelId,
        authorId
    );
    String messageCreateRequestString = objectMapper.writeValueAsString(messageCreateRequest);

    // 💡 @RequestPart("messageCreateRequest")
    MockMultipartFile messageCreateRequestPart = new MockMultipartFile(
        "messageCreateRequest", // must match the @RequestPart name in the controller
        "",
        MediaType.APPLICATION_JSON_VALUE,
        messageCreateRequestString.getBytes()
    );

    // ====== The DTO that the mocked service will return
    MessageDto mockResponseDto = new MessageDto(
        UUID.randomUUID(),
        Instant.now(),
        Instant.now(),
        "Hello!",
        channelId,
        mock(UserDto.class),
        null
    );

    when(messageService.create(any(MessageCreateRequest.class), anyList()))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(multipart("/api/messages") // multipart builder
            .file(messageCreateRequestPart))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("메세지 생성 실패 - 잘못된 요청")
  @Test
  void postMessage_withMissingRequiredPart_Failure() throws Exception {
    // ================== given ==================
    // messageCreateRequest 없음

    // 💡 @RequestPart(value = "attachments", required = false)
    // for the controller
    MockMultipartFile image1 = new MockMultipartFile(
        "attachments", // matching the @RequestPart name
        "image1.png",
        MediaType.IMAGE_PNG_VALUE,
        "test-image-data".getBytes()
    );

    // ================== when & then ==================

    mockMvc.perform(multipart("/api/messages")
//            .file(messageCreateRequestPart)
            .file(image1))
        .andExpect(status().isBadRequest());
  }

  /**
   * PATCH - update
   *
   * @throws Exception
   */
  @DisplayName("메세지 수정 테스트")
  @Test
  void patchMessage_returnsMessageDto() throws Exception {
    // ================== given ==================

    UUID messageId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    // ====== The DTOs we will send
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        "New Content"
    );

    // ====== The DTO that the mocked service will return
    MessageDto mockResponseDto = new MessageDto(
        messageId,
        Instant.now(),
        Instant.now(),
        "New Content",
        channelId,
        mock(UserDto.class),
        null
    );

    when(messageService.update(any(UUID.class), any(MessageUpdateRequest.class)))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(patch("/api/messages/" + messageId) // multipart builder
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(messageUpdateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.updatedAt").exists())
        .andExpect(jsonPath("$.content").value("New Content"))
        .andExpect(jsonPath("$.channelId").value(channelId.toString()));

  }

  @DisplayName("메세지 수정 실패 - Validation Error")
  @Test
  void patchMessage_validationError_Failure() throws Exception {
    // ================== given ==================

    UUID messageId = UUID.randomUUID();
    // ====== The DTOs we will send
    MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest(
        null // VALIDATION ERROR
    );

    // ================== when & then ==================

    mockMvc.perform(patch("/api/messages/" + messageId) // multipart builder
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(messageUpdateRequest)))
        .andExpect(status().isBadRequest());
  }

  /**
   * DELETE
   *
   * @throws Exception
   */
  @DisplayName("메세지 삭제 테스트")
  @Test
  void deleteMessage_returnsVoid() throws Exception {
    // ================== given ==================
    UUID messageId = UUID.randomUUID();
    doNothing().when(messageService).delete(messageId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/messages/" + messageId))
        .andExpect(status().isNoContent());
  }

  @DisplayName("메세지 삭제 테스트 실패 - 메세지 존재하지 않음")
  @Test
  void deleteMessage_messageNotFound_Failure() throws Exception {
    // ================== given ==================
    UUID messageId = UUID.randomUUID();
    doThrow(new MessageNotFoundException(messageId))
        .when(messageService)
        .delete(messageId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/messages/" + messageId))
        .andExpect(status().isNotFound());
  }

  /**
   * GET - findAllByChannelId
   *
   * @throws Exception
   */
  @DisplayName("채널 Id로 모든 메세지 조회 테스트")
  @Test
  void findAllMessages_returnsPageResponseMessageDto() throws Exception {
    // ================== given ==================
    UUID channelId = UUID.randomUUID();
    Instant cursor = Instant.now().minusSeconds(3600); // 1 hour ago

    // mock messages
    List<MessageDto> mockMessages = List.of(
        new MessageDto(
            UUID.randomUUID(),
            Instant.now().minusSeconds(1800), // 30 min ago
            Instant.now().minusSeconds(1800),
            "Hello World!",
            channelId,
            mock(UserDto.class),
            List.of()
        ),
        new MessageDto(
            UUID.randomUUID(),
            Instant.now().minusSeconds(1200), // 20 min ago
            Instant.now().minusSeconds(1200),
            "How are you?",
            channelId,
            mock(UserDto.class),
            List.of()
        )
    );

    // mock the PageResponse
    PageResponse<MessageDto> mockPageResponse = new PageResponse<>(
        mockMessages,
        0,   // current page
        50,       // page size
        false,     // has next
        2L        // total elements
    );

    // mock the service call
    when(messageService.findAllByChannelId(
        eq(channelId),
        eq(cursor),
        any(Pageable.class)
    )).thenReturn(mockPageResponse);

    // ================== when & then ==================
    mockMvc.perform(get("/api/messages")
            .param("channelId", channelId.toString())
            .param("cursor", cursor.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0].content").value("Hello World!"))
        .andExpect(jsonPath("$.content[1].content").value("How are you?"))
        .andExpect(jsonPath("$.size").value(50))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.totalElements").value(2L));
  }

  @DisplayName("채널 Id로 모든 메세지 조회 실패 - 잘못된 요청")
  @Test
  void findAllMessages_invalidRequest_Failure() throws Exception {
    // ================== given ==================
    Instant cursor = Instant.now().minusSeconds(3600); // 1 hour ago

    // ================== when & then ==================
    mockMvc.perform(get("/api/messages")
            .param("cursor", cursor.toString()))
        .andExpect(status().isBadRequest());
  }
}
