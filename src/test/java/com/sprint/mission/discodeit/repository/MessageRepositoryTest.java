package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;
    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("존재하지 않는 채널 id는 빈 slice 반환")
    void findAllByChannelIdWithAuthorReturnEmptySlice() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant createdAt = Instant.now().plusSeconds(10);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channelId, createdAt, pageable);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("채널 id와 생성 시간 이전의 메시지 조회는 작성자 상태/프로필 포함 반환")
    void findAllByChannelIdWithAuthorReturnMessage() {
        // given
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "name", "description"));
        User author = userRepository.save(new User("username", "email@codeit.com", "password", null));
        userStatusRepository.save(new UserStatus(author, Instant.now()));
        Message message = messageRepository.save(new Message("content", channel, author, null));

        UUID channelId = channel.getId();
        Instant createdAt = message.getCreatedAt().plusSeconds(10);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channelId, createdAt, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("존재하지 않는 채널 id는 마지막 메시지 조회 시 빈 Optional 반환")
    void findLastMessageAtByChannelIdReturnNull() {
        // given
        UUID channelId = UUID.randomUUID();

        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channelId);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("채널 id로 가장 최신 메시지 조회 성공")
    void findLastMessageAtByChannelIdSuccess() {
        // given
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "name", "description"));
        User author = userRepository.save(new User("username", "email@codeit.com", "password", null));
        userStatusRepository.save(new UserStatus(author, Instant.now()));
        messageRepository.save(new Message("content", channel, author, null));

        // when
        Optional<Instant> result = messageRepository.findLastMessageAtByChannelId(channel.getId());

        // then
        assertThat(result).isPresent();
    }
}