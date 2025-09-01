package com.codeit.discodeit8.slice_test_repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.discodeit8.auditing_config.AuditingConfig;
import com.codeit.discodeit8.entity.Channel;
import com.codeit.discodeit8.entity.ChannelType;
import com.codeit.discodeit8.entity.Message;
import com.codeit.discodeit8.entity.User;
import com.codeit.discodeit8.repository.ChannelRepository;
import com.codeit.discodeit8.repository.MessageRepository;
import com.codeit.discodeit8.repository.UserRepository;
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
class MessageRepositoryTest {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ChannelRepository channelRepository;

  @Test
  void 이메일로_유저_검색_테스트() {
    // given
    Channel channel1 = new Channel();
    channel1.setName("test channel 1");
    channel1.setDescription("test description 1");
    channel1.setType(ChannelType.PUBLIC);

    Channel channel2 = new Channel();
    channel2.setName("test channel 2");
    channel2.setDescription("test description 2");
    channel2.setType(ChannelType.PUBLIC);

    channelRepository.save(channel1);
    channelRepository.save(channel2);

    Message message1 = new Message();
    message1.setContent("test1");
    message1.setChannel(channel1);

    Message message2 = new Message();
    message2.setContent("test2");
    message2.setChannel(channel1);

    Message message3 = new Message();
    message3.setContent("test3");
    message3.setChannel(channel2);

    messageRepository.save(message1);
    messageRepository.save(message2);
    messageRepository.save(message3);

    // when
    List<Message> messages = messageRepository.findByChannelId(channel1.getId());

    // then
    assertThat(messages.size()).isEqualTo(2);
    assertThat(messages.get(0).getContent()).isEqualTo("test1");
    assertThat(messages.get(1).getContent()).isEqualTo("test2");
  }

  @Test
  void 채널의_가장_처음_메시지_검색_테스트() {
    // given
    Channel channel1 = new Channel();
    channel1.setName("test channel 1");
    channel1.setDescription("test description 1");
    channel1.setType(ChannelType.PUBLIC);

    channelRepository.save(channel1);

    Message message1 = new Message();
    message1.setContent("test1");
    message1.setChannel(channel1);

    Message message2 = new Message();
    message2.setContent("test2");
    message2.setChannel(channel1);

    messageRepository.save(message1);
    messageRepository.save(message2);

    // when
    Optional<Message> targetMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel1.getId());
    // 가장 최근에 쓰여진 메시지를 찾는 부분
    // then
    assertThat(targetMessage.isPresent()).isTrue();
    assertThat(targetMessage.get().getCreatedAt()).isEqualTo(message2.getCreatedAt());
  }
}