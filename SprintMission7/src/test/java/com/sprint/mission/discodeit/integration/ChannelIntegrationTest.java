package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.data.ChannelDto;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChannelIntegrationTest {

  @Autowired private ChannelService channelService;

  @Autowired private ChannelRepository channelRepository;

  @Test
  @DisplayName("Public Channel 생성 성공")
  void createPublicChannel_success() {
    // given
    PublicChannelCreateRequest request = new PublicChannelCreateRequest("general", "Main channel");

    // when
    ChannelDto createdChannel = channelService.create(request);

    // then
    assertThat(createdChannel).isNotNull();
    assertThat(createdChannel.name()).isEqualTo("general");
    assertThat(channelRepository.existsById(createdChannel.id())).isTrue();
  }

  @Test
  @DisplayName("Public Channel 수정 성공")
  void updateChannel_success() {
    // given
    ChannelDto createdChannel =
        channelService.create(new PublicChannelCreateRequest("general", "Main channel"));

    // when
    PublicChannelUpdateRequest updateRequest =
        new PublicChannelUpdateRequest("updated-name", "updated-desc");
    ChannelDto updatedChannel = channelService.update(createdChannel.id(), updateRequest);

    // then
    assertThat(updatedChannel.name()).isEqualTo("updated-name");
    assertThat(updatedChannel.description()).isEqualTo("updated-desc");
  }

  @Test
  @DisplayName("Channel 삭제 성공")
  void deleteChannel_success() {
    // given
    ChannelDto createdChannel =
        channelService.create(new PublicChannelCreateRequest("general", "Main channel"));
    UUID channelId = createdChannel.id();

    // when
    channelService.delete(channelId);

    // then
    assertThat(channelRepository.existsById(channelId)).isFalse();
  }

  @Test
  @DisplayName("UserId 기반 채널 조회 (Public 포함)")
  void findAllByUserId_success() {
    // given
    ChannelDto publicChannel =
        channelService.create(new PublicChannelCreateRequest("general", "Main channel"));

    // when
    List<ChannelDto> channels = channelService.findAllByUserId(UUID.randomUUID());

    // then
    assertThat(channels).isNotEmpty();
    assertThat(channels.stream().map(ChannelDto::name)).contains(publicChannel.name());
  }
}
