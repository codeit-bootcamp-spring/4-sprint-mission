package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.test.util.ReflectionTestUtils;

public class MessageRepositoryTest extends RepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    private User firstUser;
    private User secondUser;
    private Channel firstChannel;
    private Channel secondChannel;
    private Message firstMessage;
    private Message secondMessage;
    private Message thirdMessage;

    @BeforeEach
    void setup() {
        firstUser = User.builder()
                .username("first")
                .email("first@first.com")
                .password("first")
                .build();
        secondUser = User.builder()
                .username("second")
                .email("second@second.com")
                .password("second")
                .build();
        firstChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();
        secondChannel = Channel.builder()
                .type(ChannelType.PRIVATE)
                .build();

    }

    @Test
    @DisplayName("채널에 속한 메시지를 성공적으로 조회한다")
    void findAllByChannelIdSuccess() {
        //given
        firstMessage = createMessageWithChannel(firstChannel);
        secondMessage = createMessageWithChannel(firstChannel);
        channelRepository.save(firstChannel);
        userRepository.save(firstUser);
        UUID firstChannelId = firstChannel.getId();
        messageRepository.save(firstMessage);
        messageRepository.save(secondMessage);

        //when
        List<Message> messages = messageRepository.findAllByChannelId(firstChannelId);

        //then
        assertThat(messages).hasSize(2);

    }

    @Test
    @DisplayName("없는 채널의 메시지 조회 시도 시 빈 리스트를 반환한다")
    void findAllByChannelIdWillReturnEmptyListWhenChannelNotExist() {
        //given
        UUID notExistChannelId = UUID.randomUUID();

        //when
        List<Message> message = messageRepository.findAllByChannelId(notExistChannelId);

        //then
        Assertions.assertThat(message).isEmpty();

    }


    @Test
    @DisplayName("채널에 속한 메시지를 성공적으로 삭제한다")
    void deleteAllByChannelIdSuccess() {
        //given
        firstMessage = createMessageWithChannel(firstChannel);
        secondMessage = createMessageWithChannel(firstChannel);

        channelRepository.save(firstChannel);
        userRepository.save(firstUser);
        messageRepository.save(firstMessage);
        messageRepository.save(secondMessage);
        UUID firstChannelId = firstChannel.getId();

        //when
        messageRepository.deleteAllByChannelId(firstChannelId);

        //then
        assertThat(messageRepository.findAllByChannelId(firstChannelId)).isEmpty();
    }

    @Test
    @DisplayName("채널 ID와 생성 시간으로 메시지를 페이징하여 조회한다")
    void findPagedMessageByChannelIdAndCreatedAt() {
        //given
        Message firstMessage = createMessageWithChannel(firstChannel);
        Message secondMessage = createMessageWithChannel(firstChannel);
        Message thirdMessage = createMessageWithChannel(firstChannel);

        channelRepository.save(firstChannel);
        userRepository.save(firstUser);
        messageRepository.save(firstMessage);
        messageRepository.save(secondMessage);
        messageRepository.save(thirdMessage);

        Instant now = Instant.now();
        Instant beforeFiveMinutes = now.minus(5, ChronoUnit.MINUTES);
        Instant afterFiveMinutes = now.plus(5, ChronoUnit.MINUTES);

        ReflectionTestUtils.setField(firstMessage, "createdAt", beforeFiveMinutes);
        ReflectionTestUtils.setField(secondMessage, "createdAt", now);
        ReflectionTestUtils.setField(thirdMessage, "createdAt", afterFiveMinutes);

        UUID firstChannelId = firstChannel.getId();

        //when
        Slice<Message> message = messageRepository.findChannelMessagesByCursor(firstChannelId, afterFiveMinutes,
                PageRequest.of(0, 2));

        //then
        Assertions.assertThat(message.getContent()).hasSize(2);

        //when
        message = messageRepository.findChannelMessagesByCursor(firstChannelId, afterFiveMinutes, PageRequest.of(1, 2));

        //then
        Assertions.assertThat(message.getContent()).hasSize(1);

    }

    private Message createMessageWithChannel(Channel channel) {
        return Message.builder()
                .channel(channel)
                .author(firstUser)
                .content("test")
                .build();
    }
}
