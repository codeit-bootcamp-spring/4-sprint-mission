package com.codeit.discodeit8.integration_test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codeit.discodeit8.auditing_config.AuditingConfig;
import com.codeit.discodeit8.dto.channel_service_dto.CreatePublicChannelRequestDto;
import com.codeit.discodeit8.dto.channel_service_dto.PrivateChannelCreateRequest;
import com.codeit.discodeit8.dto.channel_service_dto.PublicChannelUpdateRequest;
import com.codeit.discodeit8.dto.message_service_dto.MessageCreateRequest;
import com.codeit.discodeit8.dto.message_service_dto.MessageDto;
import com.codeit.discodeit8.dto.message_service_dto.MessageUpdateRequest;
import com.codeit.discodeit8.entity.Channel;
import com.codeit.discodeit8.entity.ChannelType;
import com.codeit.discodeit8.entity.Message;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.repository.ChannelRepository;
import com.codeit.discodeit8.repository.MessageRepository;
import com.codeit.discodeit8.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

@Import(AuditingConfig.class)
class MessageIntegrationTest extends IntegrationTestBasic {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private UserRepository userRepository;

  private Message defaultMessage;
  private Channel defaultChannel;
  private User defaultUser;

  @BeforeEach
  void setUp() {
    channelRepository.deleteAll();
    messageRepository.deleteAll();
    userRepository.deleteAll();

    // 디폴트 유저 생성
    defaultUser = User.builder()
        .username("defaultUser")
        .email("default@example.com")
        .password("pw1234")
        .status(null)
        .profile(null)
        .build();

    defaultUser = userRepository.save(defaultUser);

    // 디폴트 채널 생성
    defaultChannel = Channel.builder()
        .name("Default Channel")
        .description("Default Description")
        .type(ChannelType.PUBLIC)
        .build();

    defaultChannel = channelRepository.save(defaultChannel);

    // 디폴트 메시지 생성
    defaultMessage = Message.builder()
        .content("Hello, this is a default message.")
        .channel(defaultChannel)
        .author(defaultUser) // 작성자 설정
        .attachments(List.of()) // 첨부 없음
        .build();

    messageRepository.save(defaultMessage);
  }

  @Test
  void 메시지_생성_성공_테스트() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest(
        "Test Message",
        defaultChannel.getId(),
        defaultUser.getId()
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile filePart = new MockMultipartFile(
        "attachments",
        "test.txt",
        "text/plain",
        "Test File".getBytes()
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/messages")
            .file(jsonPart)
            .file(filePart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("Test Message"))
        .andExpect(jsonPath("$.author.id").value(defaultUser.getId().toString()))
        .andExpect(jsonPath("$.channelId").value(defaultChannel.getId().toString()))
        .andExpect(jsonPath("$.attachments").isArray());
  }

  @Test
  void 채널이_없어서_메시지_생성_실패_테스트() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest(
        "Test Message",
        UUID.randomUUID(),
        defaultUser.getId()
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile filePart = new MockMultipartFile(
        "attachments",
        "test.txt",
        "text/plain",
        "Test File".getBytes()
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/messages")
            .file(jsonPart)
            .file(filePart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }


  @Test
  void 유저가_없어서_메시지_생성_실패_테스트() throws Exception {
    // given
    MessageCreateRequest request = new MessageCreateRequest(
        "Test Message",
        defaultChannel.getId(),
        UUID.randomUUID()
    );

    MockMultipartFile jsonPart = new MockMultipartFile(
        "messageCreateRequest",
        "",
        "application/json",
        objectMapper.writeValueAsBytes(request)
    );

    MockMultipartFile filePart = new MockMultipartFile(
        "attachments",
        "test.txt",
        "text/plain",
        "Test File".getBytes()
    );

    // when
    ResultActions resultActions = mockMvc.perform(
        multipart("/api/messages")
            .file(jsonPart)
            .file(filePart)
            .contentType(MediaType.MULTIPART_FORM_DATA)
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  void 메시지_삭제_성공_테스트() throws Exception {
    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/messages/{messageId}", defaultMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNoContent());
  }

  @Test
  void 메세지가_없어서_메시지_삭제_실패_테스트() throws Exception {
    // given
    UUID randomId = UUID.randomUUID();

    // when
    ResultActions resultActions = mockMvc.perform(
        delete("/api/messages/{messageId}", randomId)
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  void 메시지_수정_성공_테스트() throws Exception {
    // given
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("new Content");

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/messages/{messageId}", defaultMessage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
    );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(defaultMessage.getId().toString()))
        .andExpect(jsonPath("$.content").value("new Content"))
        .andExpect(jsonPath("$.author.id").value(defaultUser.getId().toString()))
        .andExpect(jsonPath("$.channelId").value(defaultChannel.getId().toString()));
  }

  @Test
  void 메시지가_없어서_메시지_수정_실패_테스트() throws Exception {
    // given
    MessageUpdateRequest updateRequest = new MessageUpdateRequest("Updated Content");

    // when
    ResultActions resultActions = mockMvc.perform(
        patch("/api/messages/{messageId}", UUID.randomUUID())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest))
    );

    // then
    resultActions.andExpect(status().isNotFound());
  }

  @Test
  void 메시지_조회_성공_테스트() throws Exception {
    // given
    for (int i = 1; i <= 5; i++) {
      Message message = Message.builder()
          .content("Message " + i)
          .author(defaultUser)
          .channel(defaultChannel)
          .build();
      messageRepository.save(message);
    }

    // when
    ResultActions resultActions = mockMvc.perform(
        get("/api/messages")
            .param("channelId", defaultChannel.getId().toString())
            .param("page", "0")
            .param("size", "30") // size=3 으로 요청
            .param("sort", "createdAt,DESC")
            .contentType(MediaType.APPLICATION_JSON)
    );

    // then
    resultActions.andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.totalElements").value(6));
  }
}