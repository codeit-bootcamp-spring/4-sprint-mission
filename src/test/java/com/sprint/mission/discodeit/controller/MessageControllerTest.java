package com.sprint.mission.discodeit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.constant.MessageErrorCode;
import com.sprint.mission.discodeit.dto.PageResponse;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.service.MessageService;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Test
    @DisplayName("텍스트만 포함한 메시지 생성에 성공한다")
    void createTextMessage_Success() throws Exception {
        //given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("test", channelId, authorId);

        User user = new User("testUser", "testUser", "testUser", null);

        ReflectionTestUtils.setField(user, "id", authorId);

        Channel channel = Channel.builder()
                .name("testChannel")
                .description("description")
                .type(ChannelType.PUBLIC)
                .build();

        Message message = new Message("test", channel, user);

        ReflectionTestUtils.setField(message, "id", messageId);

        UserResponse userResponse = new UserResponse(user);

        MessageResponse messageResponse = new MessageResponse(messageId, Instant.now(),Instant.now(),"test", channelId, userResponse, new ArrayList<>());

        given(messageService.createMessage(any(MessageCreateServiceRequest.class))).willReturn(messageResponse);

        //when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(messageCreateRequest).getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
        )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("test"))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.author.username").value("testUser"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));

    }

    @Test
    @DisplayName("이미지를 여러개 포함한 메시지 생성에 성공한다")
    void createImageMessage_Success() throws Exception {
        //given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("content", channelId, authorId);

        User user = new User("testUser", "testUser", "testUser", null);

        ReflectionTestUtils.setField(user, "id", authorId);

        Channel channel = Channel.builder()
                .name("testChannel")
                .description("description")
                .type(ChannelType.PUBLIC)
                .build();

        Message message = new Message("test", channel, user);

        ReflectionTestUtils.setField(message, "id", messageId);

        UserResponse userResponse = new UserResponse(user);

        MessageResponse messageResponse = new MessageResponse(messageId, Instant.now(),Instant.now(),"test", channelId, userResponse, new ArrayList<>());

        MockMultipartFile file =new MockMultipartFile("attachments", "test.jpg", "image/jpeg", "test".getBytes());
        MockMultipartFile file2 =new MockMultipartFile("attachments", "test2.jpg", "image/jpeg", "test2".getBytes());


        given(messageService.createMessage(any(MessageCreateServiceRequest.class))).willReturn(messageResponse);

        //when & then
        mockMvc.perform(multipart("/api/messages")
                        .file(new MockMultipartFile("messageCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(messageCreateRequest).getBytes()))
                        .file(file)
                        .file(file2)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("test"))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.author.username").value("testUser"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("메시지 수정에 성공한다")
    void updateMessage_Success() throws Exception {
        //given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("update", channelId, authorId);

        User user = new User("testUser", "testUser", "testUser", null);

        ReflectionTestUtils.setField(user, "id", authorId);

        Channel channel = Channel.builder()
                .name("testChannel")
                .description("description")
                .type(ChannelType.PUBLIC)
                .build();

        Message message = new Message("test", channel, user);

        ReflectionTestUtils.setField(message, "id", messageId);

        UserResponse userResponse = new UserResponse(user);

        MessageResponse messageResponse = new MessageResponse(messageId, Instant.now(),Instant.now(),"update", channelId, userResponse, new ArrayList<>());

        given(messageService.updateContent(any(MessageUpdateServiceRequest.class))).willReturn(messageResponse);

        //when & then
        mockMvc.perform(patch("/api/messages/{messageId}", messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageUpdateRequest))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("update"))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.author.username").value("testUser"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()));
    }

    @Test
    @DisplayName("메시지 삭제에 성공한다")
    void deleteMessage_Success() throws Exception {
        //given
        UUID messageId = UUID.randomUUID();

        willDoNothing().given(messageService).deleteMessage(any(UUID.class));
        //when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("없는 메시지 삭제에 실패한다")
    void deleteMessage_Fail_WhenMessageNotExist() throws Exception {
        //given
        UUID messageId = UUID.randomUUID();

        willThrow(new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND)).given(messageService).deleteMessage(any(UUID.class));
        //when & then
        mockMvc.perform(delete("/api/messages/{messageId}", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("채널 메시지 페이징 조회를 성공한다")
    void findAllByChannelId_Success() throws Exception {
        //given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID firstMessageId = UUID.randomUUID();
        UUID secondMessageId = UUID.randomUUID();

        Instant cursor = Instant.now();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");

        User user = new User("testUser", "testUser", "testUser", null);

        ReflectionTestUtils.setField(user, "id", authorId);

        Channel channel = Channel.builder()
                .name("testChannel")
                .description("description")
                .type(ChannelType.PUBLIC)
                .build();

        Message firstMessage = new Message("test", channel, user);
        ReflectionTestUtils.setField(firstMessage, "id", firstMessageId);

        Message secondMessage = new Message("test2", channel, user);
        ReflectionTestUtils.setField(secondMessage, "id", secondMessageId);

        List<MessageResponse> messageResponses = new ArrayList<>();
        messageResponses.add(new MessageResponse(firstMessage.getId(), Instant.now(), Instant.now(), "test", channelId, new UserResponse(user), new ArrayList<>()));
        messageResponses.add(new MessageResponse(secondMessage.getId(), Instant.now(), Instant.now(), "test2", channelId, new UserResponse(user), new ArrayList<>()));

        PageResponse<MessageResponse> pageResponse = new PageResponse<>(
                pageable.getPageSize(),
                true,
                messageResponses,
                cursor.minusSeconds(10),
                (long) messageResponses.size()
        );

        given(messageService.findMessagesByChannelId(any(UUID.class), any(Instant.class), any(Pageable.class))).willReturn(pageResponse);

        //when & then
        mockMvc.perform(get("/api/messages")
                        .param("channelId", channelId.toString())
                        .param("cursor", cursor.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(messageResponses.get(0).getId().toString()))
                .andExpect(jsonPath("$.content[0].content").value(messageResponses.get(0).getContent()))
                .andExpect(jsonPath("$.content[0].author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.content[0].author.username").value("testUser"))
                .andExpect(jsonPath("$.content[0].channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.content[1].id").value(messageResponses.get(1).getId().toString()))
                .andExpect(jsonPath("$.content[1].content").value(messageResponses.get(1).getContent()))
                .andExpect(jsonPath("$.content[1].author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.content[1].author.username").value("testUser"))
                .andExpect(jsonPath("$.content[1].channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.hasNext").value(true))
                .andExpect(jsonPath("$.totalElements").value(2));
    }
}
