package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.fixture.ChannelFixture;
import com.sprint.mission.discodeit.repository.fixture.UserFixture;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import({GlobalExceptionHandler.class})
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper om;

    @MockBean
    private MessageService messageService;
    @MockBean
    private MessageMapper messageMapper;
    @MockBean
    private BinaryContentMapper binaryContentMapper;
    @MockBean
    private BinaryContentService binaryContentService;
    @MockBean
    private PageResponseMapper pageResponseMapper;

    @DisplayName("메시지를 생성합니다. 작성자와 채널은 필수 항목이며 메시지 내용과 첨부파일은 선택사항입니다." +
            "첨부 파일이 있으면 내용이 없어도 되고 내용이 있으면 첨부파일은 없어도 동작합니다." +
            "웹에서 메시지와 첨부파일이 없는 메시지는 보내지 않습니다.")
    @Test
    void createMessageNoAttachments() throws Exception {
        // 본문 생성
        User user = UserFixture.createUserHong();
        user.setId(UUID.randomUUID());
        Channel channel = ChannelFixture.createPublicChannel();
        channel.setId(UUID.randomUUID());

        MessageCreateRequest request = new MessageCreateRequest(user.getId(), channel.getId(), "content");
        String content = om.writeValueAsString(request);
        MockMultipartFile messageCreateRequest = new MockMultipartFile("messageCreateRequest", "jsondata", MediaType.APPLICATION_JSON_VALUE, content.getBytes());

        // given
        Message message = Message.of(UUID.randomUUID(), channel, user, "content");
        given(messageService.createMessage(request, new ArrayList<>())).willReturn(message);
        UserDto userDto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), null, true);
        MessageDto messageDto = new MessageDto(message.getId(), userDto, channel.getId(), "content", null, Instant.now(), Instant.now());
        given(messageMapper.toDto(message)).willReturn(messageDto);

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/messages")
                        .file(messageCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.author.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.channelId").value(channel.getId().toString()));
    }

    @DisplayName("유저 또는 채널의 정보 없이는 메시지를 생성할 수 없습니다.")
    @Test
    void createMessageShouldFailedWhenUserOrChannelIsNull() throws Exception {
        // 본문 생성 given
        MessageCreateRequest request = new MessageCreateRequest(null, null, null);
        String content = om.writeValueAsString(request);
        MockMultipartFile messageCreateRequest = new MockMultipartFile("messageCreateRequest", null, "application/json", content.getBytes());

        // when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/messages")
                        .file(messageCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors[*].reason", "잘못된 ID 입니다.").exists());
    }
}
