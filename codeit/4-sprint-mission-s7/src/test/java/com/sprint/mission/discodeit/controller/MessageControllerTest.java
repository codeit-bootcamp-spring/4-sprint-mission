package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.advice.GlobalExceptionHandler;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@Import(GlobalExceptionHandler.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MessageService messageService;

    @Test
    void 첨부파일없이_메세지_생성_성공() throws Exception {
        //given
        MessageCreateRequest request = new MessageCreateRequest("only text", UUID.randomUUID(), UUID.randomUUID());
        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest", "messageCreateRequest",
                MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        UUID messageId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Instant now = Instant.now();

        UserDto authorDto = new UserDto(authorId, "kim", "kim@nate.com", null, true);

        MessageDto onlyText = new MessageDto(messageId, now, now, "only text", channelId, authorDto, List.of());

        when(messageService.create(any(MessageCreateRequest.class), any())).thenReturn(onlyText);

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/messages")
                        .file(messageCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.content").value("only text"))
                .andExpect(jsonPath("$.channelId").value(channelId.toString()))
                .andExpect(jsonPath("$.author.id").value(authorId.toString()))
                .andExpect(jsonPath("$.author.username").value("kim"))
                .andExpect(jsonPath("$.author.email").value("kim@nate.com"))
                .andExpect(jsonPath("$.attachments").isArray())
                .andExpect(jsonPath("$.attachments").isEmpty());
    }

    @Test
    void 유저_정보와_채널_정보_없어_메세지_생성_실패() throws Exception {
        //given
        MessageCreateRequest request = new MessageCreateRequest("only text", null, null);
        String content = objectMapper.writeValueAsString(request);
        MockMultipartFile messageCreateRequest = new MockMultipartFile(
                "messageCreateRequest", "messageCreateRequest",
                MediaType.APPLICATION_JSON_VALUE, content.getBytes(StandardCharsets.UTF_8));

        //when
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/messages")
                        .file(messageCreateRequest)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        );

        //then
        resultActions.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED")) // 실제 응답에 맞게 수정
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details.authorId").value("authorId는 null일 수 없습니다"))
                .andExpect(jsonPath("$.details.channelId").value("channelId는 null일 수 없습니다"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.exceptionType").value("DiscodeitException"));
    }
}
