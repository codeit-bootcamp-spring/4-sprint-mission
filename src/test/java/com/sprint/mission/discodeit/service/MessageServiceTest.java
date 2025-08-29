package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChannelRepository channelRepository;

    @InjectMocks
    private BasicMessageService messageService;

    @DisplayName("정상적으로 메시지 생성 완료")
    @Test
    void messageCreateOk() {
        // given
        UUID authorId = UUID.randomUUID();
        User user = User.of(authorId, "username", "test@email.com", "password", null);
        UUID channelId = UUID.randomUUID();
        Channel channel = Channel.of(channelId, "name", "description", ChannelType.PUBLIC);
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "content");
        Message message = new Message(request.content(), channel, user);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(user));
        given(messageRepository.save(message)).willReturn(message);

        // when
        Message createdMessage = messageService.createMessage(request, null);

        // then
        assertEquals(user, createdMessage.getAuthor());
        assertEquals(channel, createdMessage.getChannel());
        assertEquals(request.content(), createdMessage.getContent());
        then(userRepository).should(times(1)).findById(authorId);
        then(channelRepository).should(times(1)).findById(channelId);
        then(messageRepository).should(times(1)).save(message);
    }

    @DisplayName("유저를 찾을 수 없는 경우 UserNotFoundException 발생")
    @Test
    void messageCreateShouldFailedWhenUserNotFound() {
        // given
        UUID authorId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "content");
        given(channelRepository.findById(channelId)).willReturn(Optional.of(new Channel()));
        given(userRepository.findById(authorId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(UserNotFoundException.class, () -> messageService.createMessage(request, null));
        assertEquals("User Not Found", exception.getMessage());
        assertInstanceOf(UserNotFoundException.class, exception);
    }

    @DisplayName("채널을 찾을 수 없는 경우 ChannelNotFoundException 발생")
    @Test
    void messageCreateShouldFailedWhenChannelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest(authorId, channelId, "content");
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(ChannelNotFoundException.class, () -> messageService.createMessage(request, null));
        assertEquals("Channel Not Found", exception.getMessage());
        assertInstanceOf(ChannelNotFoundException.class, exception);
    }

    @DisplayName("정상적으로 메시지 수정 완료")
    @Test
    void messageUpdateOk() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");
        Message message = Message.of(messageId, new Channel(), new User(), "oldContent");
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageRepository.save(message)).willReturn(message);

        // when
        Message updatedMessage = messageService.updateMessage(messageId, request);

        // then
        assertEquals(request.newContent(), updatedMessage.getContent());
        then(messageRepository).should(times(1)).findById(messageId);
        then(messageRepository).should(times(1)).save(message);
        assertEquals(message.getId(), updatedMessage.getId());
    }

    @DisplayName("해당하는 메시지를 찾을 수 없는 경우 MessageNotFoundException 발생")
    @Test
    void messageUpdateShouldFailedMessageNotFound() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("newContent");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(MessageNotFoundException.class, () -> messageService.updateMessage(messageId, request));
        assertEquals("Message Not Found", exception.getMessage());
        assertInstanceOf(MessageNotFoundException.class, exception);
    }

    @DisplayName("정상적으로 메시지 삭제 완료")
    @Test
    void messageDeleteOk() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = Message.of(messageId, new Channel(), new User(), "oldContent");
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.removeMessage(messageId);

        // when
        then(messageRepository).should(times(1)).delete(message);
    }

    @DisplayName("해당하는 메시지가 존재하지 않을 경우 MessageNotFoundException 발생")
    @Test
    void messageDeleteShouldFailedWhenMessageNotFound() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = Message.of(messageId, new Channel(), new User(), "oldContent");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(MessageNotFoundException.class, () -> messageService.removeMessage(messageId));
        assertEquals("Message Not Found", exception.getMessage());
        assertInstanceOf(MessageNotFoundException.class, exception);
    }

    @DisplayName("채널 Id로 정상적으로 Message 조회 완료")
    @Test
    void messageFindAllByChannelIdOkShouldReturnAllMessage() {
        // given
        UUID messageId = UUID.randomUUID();
        UUID channelId = UUID.randomUUID();
        Message message = Message.of(messageId, new Channel(), new User(), "oldContent");
        Message message2 = Message.of(messageId, new Channel(), new User(), "newContent");
        Pageable pageable = PageRequest.of(0, 10);
        Instant cursor = Instant.now();
        Page<Message> page = new PageImpl<Message>(List.of(message, message2), pageable, 2);
        given(channelRepository.findById(channelId)).willReturn(Optional.of(new Channel()));
        given(messageRepository.findAllByChannel_Id(channelId, pageable)).willReturn(page);

        // when
        Page<Message> messages = messageService.findAllByChannelId(channelId, cursor, pageable);

        // then
        assertEquals(message, messages.getContent().get(0));
        assertEquals(message2, messages.getContent().get(1));
        assertEquals(2, messages.getTotalElements());
        assertInstanceOf(Page.class, messages);
    }

    @DisplayName("채널이 존재하지 않는 경우 ChannelNotFoundException 발생")
    @Test
    void messageFindAllByChannelIdShouldFailedWhenChannelNotFound() {
        // given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        Instant cursor = Instant.now();
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        Exception exception = assertThrows(ChannelNotFoundException.class, () -> messageService.findAllByChannelId(channelId, cursor, pageable));
        assertEquals("Channel Not Found", exception.getMessage());
        assertInstanceOf(ChannelNotFoundException.class, exception);
    }
}
