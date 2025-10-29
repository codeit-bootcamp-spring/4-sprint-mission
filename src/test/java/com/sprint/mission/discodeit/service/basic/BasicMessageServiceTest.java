package com.sprint.mission.discodeit.service.basic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import com.sprint.mission.discodeit.constant.MessageErrorCode;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.request.MessageCreateServiceRequest;
import com.sprint.mission.discodeit.dto.message.request.MessageUpdateServiceRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ChannelException;
import com.sprint.mission.discodeit.exception.MessageException;
import com.sprint.mission.discodeit.exception.UserException;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.mapper.PageResponseMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageAttachmentRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MockTest;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

public class BasicMessageServiceTest extends MockTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentRepository binaryContentRepository;

    @Mock
    private BinaryContentStorage binaryContentStorage;

    @Mock
    private MessageAttachmentRepository messageAttachmentRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private PageResponseMapper pageResponseMapper;

    @InjectMocks
    private BasicMessageService messageService;

    private UUID userId;
    private UUID channelId;
    private UUID messageId;

    private User user;
    private Channel channel;
    private Message message;
    private Message fileMessage;
    private Message textAndFileMessage;

    private MessageCreateServiceRequest textMessageCreateServiceRequest;
    private MessageCreateServiceRequest fileMessageCreateServiceRequest;
    private MessageCreateServiceRequest textAndFileMessageCreateServiceRequest;
    private MessageUpdateServiceRequest messageUpdateServiceRequest;
    private MessageResponse messageResponse;

    @BeforeEach
    void setUp() {
        user = new User("testUser", "testUser", "testUser", null);

        channel = Channel.builder()
                .name("testChannel")
                .description("testChannel")
                .type(ChannelType.PUBLIC)
                .build();

        message = Message.builder()
                .content("testMessage")
                .channel(channel)
                .author(user)
                .build();

        messageId = UUID.randomUUID();
        ReflectionTestUtils.setField(message, "id", messageId);

        MockMultipartFile file = new MockMultipartFile(
                "testFile",
                "testFile",
                "testFile",
                new byte[0]
        );

        fileMessage = Message.builder()
                .content("testFileMessage")
                .channel(channel)
                .author(user)
                .build();

//        fileMessage.addAttachment(file);

        textMessageCreateServiceRequest = MessageCreateServiceRequest.builder()
                .channelId(channel.getId())
                .userId(user.getId())
                .build();

        fileMessageCreateServiceRequest = MessageCreateServiceRequest.builder()
                .channelId(channel.getId())
                .userId(user.getId())
                .build();

        textAndFileMessageCreateServiceRequest = MessageCreateServiceRequest.builder()
                .channelId(channel.getId())
                .userId(user.getId())
                .message("textAndFileMessage")
                .build();

        ReflectionTestUtils.setField(fileMessageCreateServiceRequest, "attachments", List.of(file));
        ReflectionTestUtils.setField(textAndFileMessageCreateServiceRequest, "attachments", List.of(file));

        messageUpdateServiceRequest = MessageUpdateServiceRequest.builder()
                .messageId(message.getId())
                .content("updateMessage")
                .build();

        messageResponse = new MessageResponse(
                message.getId(),
                message.getCreatedAt(),
                message.getUpdatedAt(),
                message.getContent(),
                message.getChannel().getId(), null, null);

    }

    @Test
    @DisplayName("텍스트만 포함한 메시지 생성 성공")
    void createMessageOnlyContainTextSuccess() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(messageMapper.toEntity(any(MessageCreateServiceRequest.class), any(User.class), any(Channel.class))).willReturn(message);
        given(messageRepository.save(any(Message.class))).willReturn(message);
        given(messageMapper.toResponse(any(Message.class))).willReturn(messageResponse);

        //when
        MessageResponse result = messageService.createMessage(textMessageCreateServiceRequest);

        //then
        assertThat(result).isEqualTo(messageResponse);
        then(messageRepository).should(times(1)).save(any(Message.class));
    }

    @Test
    @DisplayName("파일만 포함하는 메시지 생성 성공")
    void createMessageContainsOnlyFileSuccess() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(messageMapper.toEntity(any(MessageCreateServiceRequest.class), any(User.class), any(Channel.class))).willReturn(message);
        given(messageRepository.save(any(Message.class))).willReturn(fileMessage);
        given(messageMapper.toResponse(any(Message.class))).willReturn(messageResponse);
        given(binaryContentRepository.saveAll(any(List.class))).willReturn(null);


        //when
        MessageResponse result = messageService.createMessage(fileMessageCreateServiceRequest);

        //then
        assertThat(result).isEqualTo(messageResponse);
        then(messageRepository).should(times(1)).save(any(Message.class));
        then(binaryContentRepository).should(times(1)).saveAll(any(List.class));

    }

    @Test
    @DisplayName("파일과 메시지를 포함하는 메시지 생성 성공")
    void createMessageContainsTextAndMessage() {
        //given
        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(messageMapper.toEntity(any(MessageCreateServiceRequest.class), any(User.class), any(Channel.class))).willReturn(message);
        given(messageRepository.save(any(Message.class))).willReturn(textAndFileMessage);
        given(messageMapper.toResponse(any(Message.class))).willReturn(messageResponse);
        given(binaryContentRepository.saveAll(any(List.class))).willReturn(null);


        //when
        MessageResponse result = messageService.createMessage(fileMessageCreateServiceRequest);

        //then
        assertThat(result).isEqualTo(messageResponse);
        then(messageRepository).should(times(1)).save(any(Message.class));
        then(binaryContentRepository).should(times(1)).saveAll(any(List.class));

    }

    @Test
    @DisplayName("존재하지 않는 채널에 메시지 생성 시도 시 실패")
    void createMessageFailWhenChannelDoesNotExist() {
        //given
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> messageService.createMessage(textMessageCreateServiceRequest))
                .isInstanceOf(ChannelException.class);
    }

    @Test
    @DisplayName("존재하지 않는 사용자가 메시지 생성 시도 시 실패")
    void createMessageFailWhenUserDoesNotExist() {
        //given
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> messageService.createMessage(textMessageCreateServiceRequest))
                .isInstanceOf(UserException.class);
    }

    @Test
    @DisplayName("메세지 수정 성공")
    void updateMessageSuccess() {
        //given
        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));
        given(messageMapper.toResponse(any(Message.class))).willReturn(messageResponse);

        //when
        MessageResponse result = messageService.updateContent(messageUpdateServiceRequest);

        //then
        AssertionsForClassTypes.assertThat(result).isEqualTo(messageResponse);
//        then(messageRepository).should().

    }

    @Test
    @DisplayName("없는 메시지 수정 시도 시 실패")
    void updateMessageFailWhenMessageDoesNotExist() {
        //given
        willThrow(new MessageException(MessageErrorCode.MESSAGE_NOT_FOUND)).given(messageRepository).findById(any(
                UUID.class));

        //when & then
        assertThatThrownBy(() -> messageService.updateContent(messageUpdateServiceRequest))
                .isInstanceOf(MessageException.class);
    }

    @Test
    @DisplayName("메시지 삭제 성공")
    void deleteMessageSuccess() {
        //given
        given(messageRepository.findById(any(UUID.class))).willReturn(Optional.of(message));

        //when
        messageService.deleteMessage(messageId);

        //then
        then(messageRepository).should().deleteById(messageId);
    }

}
