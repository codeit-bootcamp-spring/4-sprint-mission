package com.sprint.mission.discodeit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@DataJpaTest
public class ChannelRepositoryTest {

  @Autowired
  private ChannelRepository channelRepository;

  private Channel testChannel;

  // setup
  @BeforeEach
  public void setUp() {
    Channel testChannel1 = new Channel(
        ChannelType.PUBLIC,
        "channel 1",
        "hello"
    );
    channelRepository.save(testChannel1);

    Channel testChannel2 = new Channel(
        ChannelType.PUBLIC,
        "channel 2",
        "hello"
    );
    channelRepository.save(testChannel2);

    testChannel = testChannel1;
  }

  @DisplayName("채널 ID로 조회 테스트")
  @Test
  void findAllByTypeOrIdInTest_Id() {
    // ============ given ============
    List<UUID> targetIds = new ArrayList<>();
    targetIds.add(testChannel.getId());

    // ============ when ============
    List<Channel> channels = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PRIVATE,
        targetIds
    );

    // ============ then ============
    assertThat(channels).hasSize(1);
    assertThat(channels).contains(testChannel);
  }

  @DisplayName("채널 타입으로 조회 테스트")
  @Test
  void findAllByTypeOrIdInTest_Type() {
    // ============ given ============
    List<UUID> targetIds = new ArrayList<>();
    targetIds.add(UUID.randomUUID());
    targetIds.add(UUID.randomUUID());

    // ============ when ============
    List<Channel> channels = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PUBLIC,
        targetIds
    );

    // ============ then ============
    assertThat(channels).hasSize(2);
    assertThat(channels).contains(testChannel);
  }

  @DisplayName("채널 타입/ID로 조회 테스트 실패 - 채널 없음")
  @Test
  void findAllByTypeOrIdIn_Failure_NoMatchingChannel() {
    // ============ given ============
    List<UUID> targetIds = new ArrayList<>();
    targetIds.add(UUID.randomUUID());
    targetIds.add(UUID.randomUUID());

    // ============ when ============
    //should include public and the private channel (test)
    List<Channel> channels = channelRepository.findAllByTypeOrIdIn(
        ChannelType.PRIVATE,
        targetIds
    );

    // ============ then ============
    assertThat(channels).isEmpty();
  }
}
