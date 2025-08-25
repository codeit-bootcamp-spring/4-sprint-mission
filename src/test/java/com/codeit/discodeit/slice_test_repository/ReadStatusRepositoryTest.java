package com.codeit.discodeit.slice_test_repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.discodeit.auditing_config.AuditingConfig;
import com.codeit.discodeit.entity.Channel;
import com.codeit.discodeit.entity.ChannelType;
import com.codeit.discodeit.entity.ReadStatus;
import com.codeit.discodeit.entity.User;
import com.codeit.discodeit.entity.UserStatus;
import com.codeit.discodeit.repository.ChannelRepository;
import com.codeit.discodeit.repository.ReadStatusRepository;
import com.codeit.discodeit.repository.UserRepository;
import com.codeit.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test") // yaml 파일을 test로
@Import(AuditingConfig.class)
class ReadStatusRepositoryTest {

  @Autowired
  private UserStatusRepository userStatusRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Autowired
  private ReadStatusRepository readStatusRepository;

  @Test
  void 채널_아이디와_유저_아이디로_읽기상태_검색_성공_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    Channel channel = new Channel();
    channel.setName("test");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    ReadStatus readStatus = new ReadStatus();
    readStatus.setUser(user);
    readStatus.setChannel(channel);
    readStatusRepository.save(readStatus);

    // when
    Optional<ReadStatus> targetReadStatus =
        readStatusRepository.findByUserIdAndChannelId(user.getId(), channel.getId());

    // then
    assertThat(targetReadStatus).isPresent();
    assertThat(targetReadStatus.get().getUser().getId()).isEqualTo(user.getId());
    assertThat(targetReadStatus.get().getChannel().getId()).isEqualTo(channel.getId());
  }


  @Test
  void 채널_아이디로만_읽기상태_검색_실패_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    Channel channel = new Channel();
    channel.setName("test");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    ReadStatus readStatus = new ReadStatus();
    readStatus.setUser(user);
    readStatus.setChannel(channel);
    readStatusRepository.save(readStatus);

    // when
    Optional<ReadStatus> targetReadStatus =
        readStatusRepository.findByUserIdAndChannelId(UUID.randomUUID(), channel.getId());

    // then
    assertThat(targetReadStatus).isEmpty();
  }


  @Test
  void 아이디로만_읽기상태_검색_실패_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw1");
    userRepository.save(user);

    Channel channel = new Channel();
    channel.setName("test");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    ReadStatus readStatus = new ReadStatus();
    readStatus.setUser(user);
    readStatus.setChannel(channel);
    readStatusRepository.save(readStatus);

    // when
    Optional<ReadStatus> targetReadStatus =
        readStatusRepository.findByUserIdAndChannelId(user.getId(), UUID.randomUUID());

    // then
    assertThat(targetReadStatus).isEmpty();
  }

  @Test
  void 유저_아이디로_읽기상태_리스트_조회_테스트() {
    // given
    User user = new User();
    user.setUsername("kwon");
    user.setEmail("kwon@example.com");
    user.setPassword("pw2");
    userRepository.save(user);

    Channel channel1 = new Channel();
    channel1.setName("channel1");
    channel1.setType(ChannelType.PUBLIC);
    channelRepository.save(channel1);

    Channel channel2 = new Channel();
    channel2.setName("channel2");
    channel2.setType(ChannelType.PRIVATE);
    channelRepository.save(channel2);

    ReadStatus rs1 = new ReadStatus();
    rs1.setUser(user);
    rs1.setChannel(channel1);
    readStatusRepository.save(rs1);

    ReadStatus rs2 = new ReadStatus();
    rs2.setUser(user);
    rs2.setChannel(channel2);
    readStatusRepository.save(rs2);

    // when
    List<ReadStatus> readStatusList = readStatusRepository.findAllByUserId(user.getId());

    // then
    assertThat(readStatusList).hasSize(2);
    assertThat(readStatusList).extracting("channel").contains(channel1, channel2);
  }

  @Test
  void 채널로_읽기상태_리스트_조회_테스트() {
    // given
    User user1 = new User();
    user1.setUsername("kim");
    user1.setEmail("kim@example.com");
    user1.setPassword("pw3");
    userRepository.save(user1);

    User user2 = new User();
    user2.setUsername("park");
    user2.setEmail("park@example.com");
    user2.setPassword("pw4");
    userRepository.save(user2);

    Channel channel = new Channel();
    channel.setName("channelX");
    channel.setType(ChannelType.PUBLIC);
    channelRepository.save(channel);

    ReadStatus rs1 = new ReadStatus();
    rs1.setUser(user1);
    rs1.setChannel(channel);
    readStatusRepository.save(rs1);

    ReadStatus rs2 = new ReadStatus();
    rs2.setUser(user2);
    rs2.setChannel(channel);
    readStatusRepository.save(rs2);

    // when
    List<ReadStatus> readStatusList = readStatusRepository.findByChannel(channel);

    // then
    assertThat(readStatusList).hasSize(2);
    assertThat(readStatusList).extracting("user").contains(user1, user2);
  }

}
