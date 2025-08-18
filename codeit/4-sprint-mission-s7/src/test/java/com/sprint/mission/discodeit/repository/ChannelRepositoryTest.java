package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.JpaAuditingTestConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.fixture.ChannelFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaAuditingTestConfig.class})
public class ChannelRepositoryTest {

    @Autowired private ChannelRepository channelRepository;

    @BeforeEach
    void setUp() {
        Channel publicChannel = ChannelFixture.createPublicChannel();
        channelRepository.save(publicChannel);
        Channel privateChannel = ChannelFixture.createPrivateChannel();
        channelRepository.save(privateChannel);
    }

    @Test

    void 채널_타입이나_아이디로_조회_성공() {
        // given
        Channel channel = new Channel(ChannelType.PUBLIC, "공개방", "모든 유저가 사용가능한 방");
        Channel privateChannel = new Channel(ChannelType.PRIVATE, null, null);
        channelRepository.save(channel);
        privateChannel = channelRepository.save(privateChannel);

        List<UUID> ids = List.of(privateChannel.getId());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, ids);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).contains(privateChannel);
        assertThat(result.get(1)).isEqualTo(channel);
    }

    @Test
    void 채널_타입으로_조회_실패() {
        // given
        Channel channel = new Channel(null, "공개방", "모든 유저가 참여 가능");

        // when + then
        assertThatThrownBy(() -> {
            channelRepository.save(channel);
            channelRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);

    }
}
