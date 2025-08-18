package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.JpaAuditingTestConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.fixture.ChannelFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({JpaAuditingTestConfig.class})
public class ChannelRepositoryTest {
    @Autowired
    private ChannelRepository channelRepository;

    @BeforeEach
    void setup() {
        Channel publicChannel = ChannelFixture.createPublicChannel();
        channelRepository.save(publicChannel);
        Channel privateChannel = ChannelFixture.createPrivateChannel();
        channelRepository.save(privateChannel);
    }

    @DisplayName("공개 채널만 전체 조회합니다.")
    @Test
    void findAllByTypeShouldReturnAllPublicChannel() {
        // given
        Channel channel = new Channel("public2", "description");
        channelRepository.save(channel);

        // when
        List<Channel> publicChannels = channelRepository.findAllByType(ChannelType.PUBLIC);

        // then
        assertThat(publicChannels).hasSize(2);
        assertThat(publicChannels).contains(channel);
        assertThat(publicChannels.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
    }

    @DisplayName("타입이 정해지지 않은 채널은 DB에 저장될 수 없습니다.")
    @Test
    void saveChannelShouldFailedWhenUnknownType() {
        // given
        Channel channel = new Channel("test", "description");
        channel.setType(null);

        // when & then
        assertThatThrownBy(() -> {
            channelRepository.save(channel);
            channelRepository.flush();
        }).isInstanceOf(DataIntegrityViolationException.class);
    }
}
