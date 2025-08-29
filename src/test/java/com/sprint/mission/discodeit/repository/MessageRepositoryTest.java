package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.fixture.ChannelFixture;
import com.sprint.mission.discodeit.repository.fixture.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
public class MessageRepositoryTest {
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;

    private Channel channel;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();
        User user = UserFixture.createUserHong();
        channel = ChannelFixture.createPublicChannel();

        userRepository.save(user);
        channelRepository.save(channel);

        Message message = new Message("content", channel, user);
        messageRepository.save(message);
        Message message2 = new Message("content2", channel, user);
        messageRepository.save(message2);
    }

    @DisplayName("채널 ID를 통해 해당 채널에 존재하는 모든 메시지를 페이지네이션하여 가져옵니다.")
    @Test
    void findAllByChannelIdShouldReturnAllMessagesPage() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Message> messages = messageRepository.findAllByChannel_Id(channel.getId(), pageable);

        // then
        assertThat(messages.getTotalElements()).isEqualTo(2);
    }

    @DisplayName("페이지네이션을 위한 Pageable의 size에는 음수가 들어갈 수 없습니다.")
    @Test
    void findAllByChannelIdShouldFailedPageSizeLessThanZero() {
        // given & when & then
        assertThatThrownBy(() -> PageRequest.of(0, -1)).isInstanceOf(IllegalArgumentException.class);
    }
}
