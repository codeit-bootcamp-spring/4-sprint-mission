package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.JpaAuditingTestConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import com.sprint.mission.discodeit.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({JpaAuditingTestConfig.class})
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        User user = UserFixture.createUserKim();
        Channel channel = ChannelFixture.createPublicChannel();
        UserStatus userStatus = new UserStatus(user, Instant.now());

        userRepository.save(user);
        channelRepository.save(channel);
        userStatusRepository.save(userStatus);

        Message message1 = new Message("첫 번째 메시지", channel, user, null);
        messageRepository.save(message1);

        Message message2 = new Message("두 번째 메시지", channel, user, null);
        messageRepository.save(message2);

        Message message3 = new Message("세 번째 메시지", channel, user, null);
        messageRepository.save(message3);
    }

    @Test
    void 채널_아이디와_작성자_정보로_찾은_모든_메세지_페이징_성공() {
        //given
        Channel savedChannel = channelRepository.findAll().get(0);
        UUID channelId = savedChannel.getId();
        Instant beforeTime = Instant.now().plus(10, ChronoUnit.MINUTES);
        Pageable createdAt = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "createdAt"));

        //when
        Slice<Message> result = messageRepository.findAllByChannelIdWithAuthor(channelId, beforeTime, createdAt);

        //then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.hasNext()).isTrue();

        Message firstMessage = result.getContent().get(0);
        assertThat(firstMessage.getAuthor()).isNotNull();
        assertThat(firstMessage.getAuthor().getStatus()).isNotNull();
    }

    @Test
    void 음수_페이지_크기로_조회_시_조회_실패() {
        //given
        Channel savedChannel = channelRepository.findAll().get(0);
        UUID channelId = savedChannel.getId();
        Instant beforeTime = Instant.now().plus(10, ChronoUnit.MINUTES);

        //when + then
        assertThatThrownBy(() -> {
            Pageable invalid = PageRequest.of(0, -1);
            messageRepository.findAllByChannelIdWithAuthor(channelId, beforeTime, invalid);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page size must not be less than one");
    }
}
