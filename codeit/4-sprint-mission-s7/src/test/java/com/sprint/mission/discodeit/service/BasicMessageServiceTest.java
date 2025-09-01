package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BasicMessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock ChannelRepository channelRepository;
    @Mock UserRepository userRepository;
    @Mock MessageMapper messageMapper;
    @Mock BinaryContentStorage binaryContentStorage;
    @Mock BinaryContentRepository binaryContentRepository;
    @Mock PageResponseMapper pageResponseMapper;

    @InjectMocks BasicMessageService service;

    @Test
    void 메시지_생성_성공() {
        //given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        Channel channel = new Channel(ChannelType.PUBLIC, "공개방", "모든 유저가 접속 가능");
        User author = new User("boob", "boob@nate.com", "bob1234", null);

        byte[] bytes = new byte[]{1, 2, 3};
        MessageCreateRequest request = new MessageCreateRequest("안녕", channelId, authorId);
        BinaryContentCreateRequest createRequest = new BinaryContentCreateRequest("a.txt", "text/plain", bytes);
        List<BinaryContentCreateRequest> attachments = List.of(createRequest);

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));

        given(binaryContentRepository.save(any(BinaryContent.class)))
                .willAnswer(invocation -> {
                    BinaryContent binaryContent = invocation.getArgument(0);
                    ReflectionTestUtils.setField(binaryContent, "id", UUID.randomUUID());
                    return binaryContent;
                });
        given(binaryContentStorage.put(any(), any(byte[].class))).willReturn(null);

        given(messageRepository.save(any(Message.class))).willAnswer(returnsFirstArg());

        given(messageMapper.toDto(any(Message.class))).willReturn(mock(MessageDto.class));

        //when
        MessageDto dto = service.create(request, attachments);

        //then
        assertThat(dto).isNotNull();
        InOrder inOrder = inOrder(channelRepository, userRepository, binaryContentRepository, binaryContentStorage, messageRepository, messageMapper);
        ArgumentCaptor<Message> msgCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<BinaryContent> bcCaptor = ArgumentCaptor.forClass(BinaryContent.class);

        inOrder.verify(channelRepository).findById(channelId);
        inOrder.verify(userRepository).findById(authorId);
        inOrder.verify(binaryContentRepository, times(1)).save(bcCaptor.capture());
        inOrder.verify(binaryContentStorage, times(1)).put(any(UUID.class), aryEq(bytes));
        inOrder.verify(messageRepository).save(msgCaptor.capture());
        inOrder.verify(messageMapper).toDto(any(Message.class));
        inOrder.verifyNoMoreInteractions();

        BinaryContent savedAttachment = bcCaptor.getValue();
        assertThat(savedAttachment.getFileName()).isEqualTo("a.txt");
        assertThat(savedAttachment.getContentType()).isEqualTo("text/plain");
        assertThat(savedAttachment.getSize()).isEqualTo(3L);

        Message savedMsg = msgCaptor.getValue();
        assertThat(savedMsg.getContent()).isEqualTo("안녕");
        assertThat(savedMsg.getChannel()).isSameAs(channel);
        assertThat(savedMsg.getAuthor()).isSameAs(author);
        assertThat(savedMsg.getAttachments()).hasSize(1);
        assertThat(savedMsg.getAttachments().get(0).getFileName()).isEqualTo("a.txt");
    }
    
    @Test
    void 메시지_생성_실패() { //channelNotFound
        //given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        MessageCreateRequest request = new MessageCreateRequest("안녕", channelId, authorId);
        List<BinaryContentCreateRequest> attachments = List.of(
                new BinaryContentCreateRequest("a.txt", "text/plain", new byte[]{1}));

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        //when + then
        assertThrows(ChannelNotFoundException.class, () -> service.create(request, attachments));

        then(channelRepository).should().findById(channelId);
        then(userRepository).shouldHaveNoInteractions();
        then(binaryContentRepository).shouldHaveNoInteractions();
        then(binaryContentStorage).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    void 메세지_수정_성공() {
        //given
        UUID messageId = UUID.randomUUID();
        Channel channel = new Channel(ChannelType.PUBLIC, "공개 채널", "모든 유저가 대화할 수 있다.");
        User author = new User("alice", "alice@nate.com", "alice1234", null);
        Message existing = new Message("안녕", channel, author, Collections.emptyList());

        given(messageRepository.findById(messageId)).willReturn(Optional.of(existing));
        given(messageMapper.toDto(any(Message.class))).willReturn(mock(MessageDto.class));

        MessageUpdateRequest request = new MessageUpdateRequest("반가워.");

        //when
        MessageDto updated = service.update(messageId, request);

        //then
        assertThat(updated).isNotNull();
        InOrder inOrder = inOrder(messageRepository, messageMapper);
        ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);

        inOrder.verify(messageRepository).findById(messageId);
        inOrder.verify(messageMapper).toDto(argumentCaptor.capture());
        inOrder.verifyNoMoreInteractions();

        Message value = argumentCaptor.getValue();
        assertThat(value.getContent()).isEqualTo("반가워.");

        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    void 메세지_수정_실패() { //대상 메세지 없음
        //given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("반가워.");
        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        //when + then
        assertThrows(MessageNotFoundException.class, () -> service.update(messageId, request));

        then(messageRepository).should().findById(messageId);
        then(messageMapper).shouldHaveNoInteractions();
        then(messageRepository).should(never()).save(any(Message.class));
    }

    @Test
    void 메세지_삭제_성공() {
        //given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(true);

        //when
        service.delete(messageId);

        //then
        InOrder inOrder = inOrder(messageRepository);
        inOrder.verify(messageRepository).existsById(messageId);
        inOrder.verify(messageRepository).deleteById(messageId);
        inOrder.verifyNoMoreInteractions();

        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    void 메세지_삭제_실패() { //메세지가 존재하지 않음
        //given
        UUID messageId = UUID.randomUUID();
        given(messageRepository.existsById(messageId)).willReturn(false);

        //when + then
        assertThrows(MessageNotFoundException.class, () -> service.delete(messageId));

        then(messageRepository).should().existsById(messageId);
        then(messageRepository).should(never()).deleteById(any());
        then(messageMapper).shouldHaveNoInteractions();
    }

    @Test
    void 메세지_탐색_성공() {
        //given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));
        Instant cursor = Instant.parse(Instant.now().toString());

        Channel channel = new Channel(ChannelType.PUBLIC, "공개 채널", "모든 유저가 사용 가능한 방");
        ReflectionTestUtils.setField(channel, "id", channelId);

        User author = new User("alice", "alice@nate.com", "alice1234", null);
        UUID authorId = UUID.randomUUID();
        ReflectionTestUtils.setField(author, "id", authorId);

        Message message1 = new Message("안녕", channel, author, Collections.emptyList());
        Message message2 = new Message("어서오세요.", channel, author, Collections.emptyList());
        ReflectionTestUtils.setField(message1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(message2, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(message1, "createdAt", Instant.now());
        ReflectionTestUtils.setField(message2, "createdAt", Instant.now().minusSeconds(60));
        ReflectionTestUtils.setField(message1, "updatedAt", message1.getCreatedAt());
        ReflectionTestUtils.setField(message2, "updatedAt", message2.getCreatedAt());

        Slice<Message> slice = new SliceImpl<>(List.of(message1, message2), pageable, false);

        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable))).willReturn(slice);

        UserDto authorDto = new UserDto(authorId, "alice", "alice@nate.com", null, true);

        MessageDto dto1 = new MessageDto(
                message1.getId(),
                message1.getCreatedAt(),
                message1.getUpdatedAt(),
                message1.getContent(),
                channelId,
                authorDto,
                List.of()
        );

        MessageDto dto2 = new MessageDto(
                message2.getId(),
                message2.getCreatedAt(),
                message2.getUpdatedAt(),
                message2.getContent(),
                channelId,
                authorDto,
                List.of()
        );

        given(messageMapper.toDto(message1)).willReturn(dto1);
        given(messageMapper.toDto(message2)).willReturn(dto2);

        @SuppressWarnings("unchecked")
        PageResponse<MessageDto> pageResponse = mock(PageResponse.class);
        given(pageResponseMapper.fromSlice(Mockito.<Slice<MessageDto>>any(), eq(dto2.createdAt()))).willReturn(pageResponse);

        //when
        PageResponse<MessageDto> result = service.findAllByChannelId(channelId, cursor, pageable);

        //then
        assertThat(result).isSameAs(pageResponse);
        then(messageRepository).should().findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable));
        then(messageMapper).should(times(1)).toDto(message1);
        then(messageMapper).should(times(1)).toDto(message2);

        then(pageResponseMapper).should().fromSlice(Mockito.<Slice<MessageDto>>any(), eq(dto2.createdAt()));
    }

    @Test
    void 메세지_탐색_실패() {
        //given
        UUID channelId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 50);
        Instant cursor = Instant.now();

        RuntimeException boom = new RuntimeException("DB error");
        given(messageRepository.findAllByChannelIdWithAuthor(eq(channelId), any(Instant.class), eq(pageable)))
                .willThrow(boom);

        //when + then
        assertThrows(RuntimeException.class, () -> service.findAllByChannelId(channelId, cursor, pageable));

        then(messageMapper).shouldHaveNoInteractions();
        then(pageResponseMapper).shouldHaveNoInteractions();
    }
}
