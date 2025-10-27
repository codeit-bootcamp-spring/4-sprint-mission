package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.request.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class MessageIntegrationTest extends IntegrationTest{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("메시지 생성 통합 테스트")
    void createMessage() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());

        ResultActions resultUserActions = createUser(userPart, profilePart);
        String firstUserJsonResponse = resultUserActions.andReturn().getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(firstUserJsonResponse, UserResponse.class);
        UUID authorId = userResponse.getId();

        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");


        ResultActions resultChannelActions = createPublicChannel(channelCreateRequest);
        String jsonResponse = resultChannelActions.andReturn().getResponse().getContentAsString();
        ChannelResponse channelResponse = objectMapper.readValue(jsonResponse, ChannelResponse.class);
        UUID channelId = channelResponse.getId();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("content",channelId, authorId);
        MockMultipartFile messagePart = new MockMultipartFile("messageCreateRequest", "","application/json", objectMapper.writeValueAsString(messageCreateRequest).getBytes());
        MockMultipartFile messageImagePart = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "".getBytes());

        //when
        ResultActions resultActions = createMessage(messagePart, messageImagePart);

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.content").value("content"));

    }

    @Test
    @DisplayName("메시지 수정 통합 테스트")
    void updateMessage() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());

        ResultActions resultUserActions = createUser(userPart, profilePart);
        String firstUserJsonResponse = resultUserActions.andReturn().getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(firstUserJsonResponse, UserResponse.class);
        UUID authorId = userResponse.getId();

        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");

        ResultActions resultChannelActions = createPublicChannel(channelCreateRequest);
        String jsonResponse = resultChannelActions.andReturn().getResponse().getContentAsString();
        ChannelResponse channelResponse = objectMapper.readValue(jsonResponse, ChannelResponse.class);
        UUID channelId = channelResponse.getId();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("content",channelId, authorId);
        MockMultipartFile messagePart = new MockMultipartFile("messageCreateRequest", "","application/json", objectMapper.writeValueAsString(messageCreateRequest).getBytes());
        MockMultipartFile messageImagePart = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "".getBytes());
        ResultActions resultActions = createMessage(messagePart, messageImagePart);

        String messageJsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        MessageResponse messageResponse = objectMapper.readValue(messageJsonResponse, MessageResponse.class);
        UUID messageId = messageResponse.getId();

        MessageUpdateRequest request = new MessageUpdateRequest("updatedContent",channelId, authorId);

        //when & then
        mockMvc.perform(patch("/api/messages/{messageId}",messageId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.content").value("updatedContent"));

    }

    @Test
    @DisplayName("메시지 삭제 통합 테스트")
    void deleteMessage() throws Exception {
        //given
        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());

        ResultActions resultUserActions = createUser(userPart, profilePart);
        String firstUserJsonResponse = resultUserActions.andReturn().getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(firstUserJsonResponse, UserResponse.class);
        UUID authorId = userResponse.getId();

        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");

        ResultActions resultChannelActions = createPublicChannel(channelCreateRequest);
        String jsonResponse = resultChannelActions.andReturn().getResponse().getContentAsString();
        ChannelResponse channelResponse = objectMapper.readValue(jsonResponse, ChannelResponse.class);
        UUID channelId = channelResponse.getId();

        MessageCreateRequest messageCreateRequest = new MessageCreateRequest("content",channelId, authorId);
        MockMultipartFile messagePart = new MockMultipartFile("messageCreateRequest", "","application/json", objectMapper.writeValueAsString(messageCreateRequest).getBytes());
        MockMultipartFile messageImagePart = new MockMultipartFile("image", "test-image.jpg", "image/jpeg", "".getBytes());
        ResultActions resultActions = createMessage(messagePart, messageImagePart);

        String messageJsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        MessageResponse messageResponse = objectMapper.readValue(messageJsonResponse, MessageResponse.class);
        UUID messageId = messageResponse.getId();

        //when & then
        mockMvc.perform(delete("/api/messages/{messageId}",messageId))
                .andExpect(status().isNoContent());
    }


    ResultActions createUser(MockMultipartFile userPart, MockMultipartFile profilePart) throws Exception {
        return mockMvc.perform(multipart("/api/users")
                .file(userPart)
                .file(profilePart)
                .contentType(MediaType.MULTIPART_FORM_DATA));
    }

    private ResultActions createPublicChannel(ChannelCreateRequest request) throws Exception {
        return mockMvc.perform(
                post("/api/channels/public")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions createMessage(MockMultipartFile messagePart, MockMultipartFile messageImagePart) throws Exception {
        return mockMvc.perform(multipart("/api/messages")
                .file(messagePart)
                .file(messageImagePart)
                .contentType(MediaType.MULTIPART_FORM_DATA));
    }
}
