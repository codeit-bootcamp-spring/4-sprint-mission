package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@EnableJpaAuditing
class ChannelRepositoryTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    @DisplayName("type과 id 모두 불일치 시 빈 리스트 반환")
    void findAllByTypeOrIdInNoMatchReturnEmpty() {
        // given
        channelRepository.save(new Channel(ChannelType.PUBLIC, "name", "description"));
        List<UUID> ids = List.of(UUID.randomUUID());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PRIVATE, ids);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("type과 id 모두 만족 시 채널 반환")
    void findAllByTypeOrIdInAllMatchReturnChannel() {
        // given
        Channel channel = channelRepository.save(new Channel(ChannelType.PUBLIC, "name", "description"));
        List<UUID> ids = List.of(channel.getId());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, ids);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("type만 일치 시 채널 반환")
    void findAllByTypeOrIdInTypeMatchReturnChannel() {
        // given
        channelRepository.save(new Channel(ChannelType.PUBLIC, "name", "description"));
        List<UUID> ids = List.of(UUID.randomUUID());

        // when
        List<Channel> result = channelRepository.findAllByTypeOrIdIn(ChannelType.PUBLIC, ids);

        // then
        assertThat(result).hasSize(1);
    }
}