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
import com.sprint.mission.discodeit.dto.channel.request.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

public class ChannelIntegrationTest extends IntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공개 채널 생성 통합 테스트")
    void createPublicChannel() throws Exception {
        //given
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");

        //when
        ResultActions resultActions = createPublicChannel(channelCreateRequest);

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("공개 채널"))
                .andExpect(jsonPath("$.description").value("공개 채널"))
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.participants").isEmpty())
                .andExpect(jsonPath("$.lastMessageAt").doesNotExist());

    }

    @Test
    @DisplayName("사설 채널 통합 테스트")
    void createPrivateChannel() throws Exception {
        //given

        UserCreateRequest userCreateRequest = new UserCreateRequest("test","test@test.com","test",null);
        MockMultipartFile profilePart = new MockMultipartFile("profile", "test-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile userPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(userCreateRequest).getBytes());
        ResultActions resultActions = createUser(userPart, profilePart);

        String firstUserJsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        UserResponse userResponse = objectMapper.readValue(firstUserJsonResponse, UserResponse.class);
        UUID firstParticipantId = userResponse.getId();

        UserCreateRequest secondUserCreateRequest = new UserCreateRequest("test2","test2@test.com","test2",null);
        MockMultipartFile secondProfilePart = new MockMultipartFile("profile", "test2-profile.jpg", "image/jpeg", "".getBytes());
        MockMultipartFile secondUserPart = new MockMultipartFile("userCreateRequest", "","application/json", objectMapper.writeValueAsString(secondUserCreateRequest).getBytes());
        resultActions = createUser(secondUserPart, secondProfilePart);

        String secondUserJsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        UserResponse secondUserResponse = objectMapper.readValue(secondUserJsonResponse, UserResponse.class);
        UUID secondParticipantId = secondUserResponse.getId();

        PrivateChannelCreateRequest channelCreateRequest = new PrivateChannelCreateRequest(List.of(firstParticipantId, secondParticipantId));

        //when & then
        mockMvc.perform(
                post("/api/channels/private")
                        .content(objectMapper.writeValueAsString(channelCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.type").value(ChannelType.PRIVATE.name()))
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants[0].id").value(firstParticipantId.toString()))
                .andExpect(jsonPath("$.participants[1].id").value(secondParticipantId.toString()))
                .andExpect(jsonPath("$.lastMessageAt").doesNotExist());
    }

    @Test
    @DisplayName("채널 업데이트 테스트")
    void updateChannel() throws Exception {
        //given
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");
        ResultActions resultActions = createPublicChannel(channelCreateRequest);

        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        ChannelResponse channelResponse = objectMapper.readValue(jsonResponse, ChannelResponse.class);
        UUID channelId = channelResponse.getId();

        ChannelUpdateRequest channelUpdateRequest = new ChannelUpdateRequest("수정 채널", "수정 채널");

        //when & then
        mockMvc.perform(
                patch("/api/channels/{channelId}", channelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(channelUpdateRequest))
                    )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(channelId.toString()))
                .andExpect(jsonPath("$.type").value(ChannelType.PUBLIC.name()))
                .andExpect(jsonPath("$.name").value("수정 채널"))
                .andExpect(jsonPath("$.description").value("수정 채널"))
                .andExpect(jsonPath("$.participants").isArray())
                .andExpect(jsonPath("$.participants").isEmpty())  //참여자가 없으면 mapper 에서 빈 리스트 할당
                .andExpect(jsonPath("$.lastMessageAt").doesNotExist());

    }

    @Test
    @DisplayName("채널 삭제 통합 테스트")
    void deleteChannel() throws Exception {
        //given
        ChannelCreateRequest channelCreateRequest = new ChannelCreateRequest("공개 채널", "공개 채널");
        ResultActions resultActions = createPublicChannel(channelCreateRequest);

        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        ChannelResponse channelResponse = objectMapper.readValue(jsonResponse, ChannelResponse.class);
        UUID channelId = channelResponse.getId();

        //when & then
        mockMvc.perform(delete("/api/channels/{channelId}", channelId))
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

}
