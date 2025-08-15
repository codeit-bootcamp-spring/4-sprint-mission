package com.sprint.mission.discodeit.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.channel.ChannelUpdatePrivateChannelException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@WebMvcTest(ChannelController.class)
public class ChannelControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockitoBean
  ChannelService channelService;

  @Autowired
  private ObjectMapper objectMapper;

  /**
   * POST - create (PUBLIC)
   *
   * @throws Exception
   */
  @DisplayName("공개 채널 생성 테스트")
  @Test
  void postPublicChannel_ReturnsChannelDto() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    String name = "new Channel";
    String description = "This is new channel";
    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        name,
        description
    );
    String channelCreateRequestJson = objectMapper.writeValueAsString(channelCreateRequestDto);

    ChannelDto mockResponseDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        name,
        description,
        null,
        Instant.now()
    );

    when(channelService.create(any(PublicChannelCreateRequest.class)))
        .thenReturn(mockResponseDto);

    // ================== when & ==================

    mockMvc.perform(post("/api/channels/public")
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelCreateRequestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value((ChannelType.PUBLIC).toString()))
        .andExpect(jsonPath("$.name").value(name))
        .andExpect(jsonPath("$.description").value(description))
        .andExpect(jsonPath("$.participants").isEmpty())
        .andExpect(jsonPath("$.lastMessageAt").exists());
  }

  @DisplayName("공개 채널 생성 실패 - invalid channel name")
  @Test
  void postPublicChannel_invalidChannelName_Failure() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    String name = "."; // << INVALID
    String description = "This is new channel";
    PublicChannelCreateRequest channelCreateRequestDto = new PublicChannelCreateRequest(
        name,
        description
    );
    String channelCreateRequestJson = objectMapper.writeValueAsString(channelCreateRequestDto);

    ChannelDto mockResponseDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        name,
        description,
        null,
        Instant.now()
    );

    when(channelService.create(any(PublicChannelCreateRequest.class)))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(post("/api/channels/public")
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelCreateRequestJson))
        .andExpect(status().isBadRequest());
  }

  @DisplayName("공개 채널 생성 실패 - invalid request")
  @Test
  void postPublicChannel_invalidRequest_Failure() throws Exception {
    // ================== given ==================
    // channelCreateRequestDto 없음

    // ================== when & ==================

    mockMvc.perform(post("/api/channels/public")
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadGateway());
  }

  /**
   * POST - create (PRIVATE)
   *
   * @throws Exception
   */
  @DisplayName("비공개 채널 생성 테스트")
  @Test
  void postPrivateChannel_ReturnsChannelDto() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    PrivateChannelCreateRequest channelCreateRequestDto = new PrivateChannelCreateRequest(
        List.of(
            UUID.randomUUID(),
            UUID.randomUUID()
        )
    );
    String channelCreateRequestJson = objectMapper.writeValueAsString(channelCreateRequestDto);

    ChannelDto mockResponseDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PRIVATE,
        null,
        null,
        List.of(
            mock(UserDto.class),
            mock(UserDto.class)
        ),
        Instant.now()
    );

    when(channelService.create(any(PrivateChannelCreateRequest.class)))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(post("/api/channels/private")
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelCreateRequestJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.type").value((ChannelType.PRIVATE).toString()))
        .andExpect(jsonPath("$.name").isEmpty())
        .andExpect(jsonPath("$.description").isEmpty())
        .andExpect(jsonPath("$.participants", hasSize(2)))
        .andExpect(jsonPath("$.lastMessageAt").exists());

  }

  @DisplayName("비공개 채널 생성 실패 - invalid number of participants")
  @Test
  void postPrivateChannel_invalidNumberOfParticipants_Failure() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    PrivateChannelCreateRequest channelCreateRequestDto = new PrivateChannelCreateRequest(
        List.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID() // INVALID! should be 2 ppl
        )
    );
    String channelCreateRequestJson = objectMapper.writeValueAsString(channelCreateRequestDto);

    // ================== when & ==================

    mockMvc.perform(post("/api/channels/private")
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelCreateRequestJson))
        .andExpect(status().isBadRequest());
  }

  /**
   * PATCH - update (PUBLIC only)
   *
   * @throws Exception
   */
  @DisplayName("채널 수정 테스트")
  @Test
  void patchChannel_returnsChannelDto() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    UUID channelId = UUID.randomUUID();
    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );
    String channelUpdateRequestJson = objectMapper.writeValueAsString(channelUpdateRequest);

    ChannelDto mockResponseDto = new ChannelDto(
        UUID.randomUUID(),
        ChannelType.PUBLIC,
        newName,
        newDescription,
        null,
        Instant.now()
    );

    when(channelService.update(any(UUID.class), any(PublicChannelUpdateRequest.class)))
        .thenReturn(mockResponseDto);

    // ================== when & then ==================

    mockMvc.perform(patch("/api/channels/" + channelId)
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelUpdateRequestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.type").value((ChannelType.PUBLIC).toString()))
        .andExpect(jsonPath("$.name").value(newName))
        .andExpect(jsonPath("$.description").value(newDescription))
        .andExpect(jsonPath("$.participants").isEmpty());
  }

  @DisplayName("채널 수정 실패 - 비공개 채널")
  @Test
  void patchChannel_privateChannel_Failure() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    UUID channelId = UUID.randomUUID();
    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );
    String channelUpdateRequestJson = objectMapper.writeValueAsString(channelUpdateRequest);

    when(channelService.update(any(UUID.class), any(PublicChannelUpdateRequest.class)))
        .thenThrow(new ChannelUpdatePrivateChannelException(channelId));

    // ================== when & then ==================

    mockMvc.perform(patch("/api/channels/" + channelId)
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelUpdateRequestJson))
        .andExpect(status().isForbidden());
  }

  @DisplayName("채널 수정 실패 - 채널 존재하지 않음")
  @Test
  void patchChannel_channelDoesNotExist_Failure() throws Exception {
    // ================== given ==================

    // The DTO for the request body
    UUID channelId = UUID.randomUUID();
    String newName = "updated name";
    String newDescription = "updated description";
    PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest(
        newName,
        newDescription
    );
    String channelUpdateRequestJson = objectMapper.writeValueAsString(channelUpdateRequest);

    when(channelService.update(any(UUID.class), any(PublicChannelUpdateRequest.class)))
        .thenThrow(new ChannelNotFoundException(channelId));

    // ================== when & then ==================

    mockMvc.perform(patch("/api/channels/" + channelId)
            // @RequestBody
            .contentType(MediaType.APPLICATION_JSON)
            .content(channelUpdateRequestJson))
        .andExpect(status().isNotFound());
  }

  /**
   * DELETE
   *
   * @throws Exception
   */
  @DisplayName("채널 삭제 테스트")
  @Test
  void deleteChannel_returnsVoid() throws Exception {
    // ================== given ==================
    UUID channelId = UUID.randomUUID();
    doNothing().when(channelService).delete(channelId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/channels/" + channelId))
        .andExpect(status().isNoContent());
  }


  @DisplayName("채널 삭제 테스트 실패 - 채널 존재하지 않음")
  @Test
  void deleteChannel_channelNotFound_Failure() throws Exception {
    // ================== given ==================
    UUID channelId = UUID.randomUUID();
    doThrow(new ChannelNotFoundException(channelId))
        .when(channelService)
        .delete(channelId);

    // ================== when & then ==================
    mockMvc.perform(delete("/api/channels/" + channelId))
        .andExpect(status().isNotFound());
  }

  /**
   * GET - findAll
   *
   * @throws Exception
   */
  @DisplayName("유저 Id로 모든 메세지 조회 테스트")
  @Test
  void findAllChannels_returnsListChannelDto() throws Exception {
    // ================== given ==================

    UUID userId = UUID.randomUUID();
    UUID channelId = UUID.randomUUID();
    List<ChannelDto> channelDtoList = List.of(
        new ChannelDto(
            channelId,
            ChannelType.PUBLIC,
            "name",
            "description",
            List.of(mock(UserDto.class)),
            Instant.now()
        )
    );

    when(channelService.findAllByUserId(userId)).thenReturn(channelDtoList);

    // ================== when & then ==================

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id").value(channelId.toString()))
        .andExpect(jsonPath("$[0].type").value("PUBLIC"));
  }

  @DisplayName("유저 Id로 모든 메세지 조회 실패 - 유저 존재하지 않음")
  @Test
  void findAllChannels_userDoesNotExist_Failure() throws Exception {
    // ================== given ==================

    UUID userId = UUID.randomUUID();
    when(channelService.findAllByUserId(userId)).thenThrow(new UserNotFoundException(userId));

    // ================== when & then ==================

    mockMvc.perform(get("/api/channels")
            .param("userId", userId.toString()))
        .andExpect(status().isNotFound());
  }
}
