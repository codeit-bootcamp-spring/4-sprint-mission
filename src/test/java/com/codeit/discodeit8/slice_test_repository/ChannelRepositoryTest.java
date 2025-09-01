package com.codeit.discodeit8.slice_test_repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.discodeit8.auditing_config.AuditingConfig;
import com.codeit.discodeit8.entity.Channel;
import com.codeit.discodeit8.entity.ChannelType;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.repository.ChannelRepository;
import com.codeit.discodeit8.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
@Import(AuditingConfig.class)
class ChannelRepositoryTest{

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  void 채널_타입으로_검색_테스트() {
    // given
    Channel channel1 = new Channel();
    channel1.setName("test channel");
    channel1.setDescription("test description");
    channel1.setType(ChannelType.PUBLIC);
    channelRepository.save(channel1);

    Channel channel2 = new Channel();
    channel2.setName("test channel2");
    channel2.setDescription("test description2");
    channel2.setType(ChannelType.PRIVATE);
    channelRepository.save(channel2);

    Channel channel3 = new Channel();
    channel3.setName("test channel3");
    channel3.setDescription("test description3");
    channel3.setType(ChannelType.PUBLIC);
    channelRepository.save(channel3);

    // when
    List<Channel> targetChannelList = channelRepository.findAllByType(ChannelType.PUBLIC);

    // then
    assertThat(targetChannelList).hasSize(2); // private까지 검색 되었다면 3일 것이고 public만 검색된다면 2일 것이다.
    assertThat(targetChannelList.get(0).getType()).isEqualTo(ChannelType.PUBLIC);
    assertThat(targetChannelList.get(1).getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  void 채널_이름으로_검색_성공_테스트() {
    // given
    Channel channel = new Channel();
    channel.setName("test channel");
    channel.setDescription("test description");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    // when
    Optional<Channel> targetChannel = channelRepository.findByName("test channel");

    // then
    assertThat(targetChannel.isPresent()).isTrue();
    assertThat(targetChannel.get().getType()).isEqualTo(ChannelType.PUBLIC);
  }

  @Test
  void 채널_이름으로_검색_실패_테스트() {
    // given
    Channel channel = new Channel();
    channel.setName("test channel");
    channel.setDescription("test description");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    // when
    Optional<Channel> targetChannel = channelRepository.findByName("channel test");

    // then
    assertThat(targetChannel.isEmpty()).isTrue();
  }
}
