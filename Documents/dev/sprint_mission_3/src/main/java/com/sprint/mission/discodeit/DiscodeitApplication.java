package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import lombok.Locked;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	static UserResponseDto setupUser(UserService userService) {
		UserCreateDto dto = new UserCreateDto("코코", "koko@codeit.com", "koko1234", null);
		return userService.create(dto);
	}

	static ChannelResponseDto setupPublicChannel(ChannelService channelService) {
		PublicChannelCreateDto dto = new PublicChannelCreateDto(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
		return channelService.createPublicChannel(dto);
	}

	static ChannelResponseDto setupPrivateChannel(ChannelService channelService) {
		List<UUID> members = new ArrayList<>();
		PrivateChannelCreateDto dto = new PrivateChannelCreateDto(ChannelType.PRIVATE, "비밀 공지", members);
		return channelService.createPrivateChannel(dto);
	}

	static MessageResponseDto messageCreateTest(MessageService messageService, ChannelResponseDto channel, UserResponseDto author) {
		List<UUID> attachmentId = new ArrayList<>();
		MessageCreateDto dto = new MessageCreateDto("안녕하세요.", channel.getId(), author.getId(), attachmentId);
		System.out.println("메시지 생성: " + dto.getContent());
		return messageService.create(dto);
	}

	static ReadStatusResponseDto readStatusTest(ReadStatusService readStatusService, ChannelResponseDto channel) {
		UUID userId = UUID.randomUUID();
		Instant readAt = Instant.now();
		ReadStatusCreateDto dto = new ReadStatusCreateDto(userId, channel.getId(), readAt);
		return readStatusService.create(dto);
	}

	static

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		//Bean 소환
		UserService userService = context.getBean(UserService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		UserResponseDto user = setupUser(userService);
		ChannelResponseDto publicChannel = setupPublicChannel(channelService);
		ChannelResponseDto privateChannel = setupPrivateChannel(channelService);
		MessageResponseDto message = messageCreateTest(messageService, publicChannel, user);


	}
}
