package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableJpaAuditing
public class DiscodeitApplication {

//	static UserDto setupUser(UserService userService) {
//		UserCreateRequest dto = new UserCreateRequest("코코", "koko@codeit.com", "koko1234", null);
//		return userService.create(dto);
//	}
//
//	static ChannelDto setupPublicChannel(ChannelService channelService) {
//		PublicChannelCreateDto dto = new PublicChannelCreateDto(ChannelType.PUBLIC, "공지", "공지 채널입니다.");
//		return channelService.createPublicChannel(dto);
//	}
//
//	static ChannelDto setupPrivateChannel(ChannelService channelService) {
//		List<UUID> members = new ArrayList<>();
//		PrivateChannelCreateDto dto = new PrivateChannelCreateDto(ChannelType.PRIVATE, "비밀 공지", members);
//		return channelService.createPrivateChannel(dto);
//	}
//
//	static MessageDto messageCreateTest(MessageService messageService, ChannelDto channel, UserDto author) {
//		List<UUID> attachmentId = new ArrayList<>();
//		MessageCreateDto dto = new MessageCreateDto("안녕하세요.", channel.getChannelId(), author.getId(), attachmentId);
//		System.out.println("메시지 생성: " + dto.getContent());
//		return messageService.create(dto);
//	}
//
//	static ReadStatusDto readStatusCreateTest(ReadStatusService readStatusService, ChannelDto channel, UserDto user) {
//		Instant readAt = Instant.now();
//		ReadStatusCreateDto dto = new ReadStatusCreateDto(user.getId(), channel.getChannelId(), readAt);
//		return readStatusService.create(dto);
//	}
//
//	static UserStatusDto userStatusCreateTest(UserStatusService userStatusService, UserDto user) {
//		UUID id = UUID.randomUUID();
//		Instant lastAccessedAt = Instant.now();
//		UserStatusCreateDto dto = new UserStatusCreateDto(id, user.getId(), lastAccessedAt);
//		return userStatusService.create(dto);
//	}

//	static BinaryContentResponseDto binaryContentCreateTest(BinaryContentService binaryContentService, UserResponseDto user, MessageResponseDto message) {
//		MultipartFile file = new MockMultipartFile(
//				"file", "profile.png","image/png",new byte[]{1, 2, 3, 4, 5} );
//		BinaryContentCreateDto dto = new BinaryContentCreateDto(user.getId(),message.getMessageId(), file, "profile.png", "img/png");
//		return binaryContentService.create(dto);
//	}

	public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		System.out.println("Hello from main");
//
//        //Bean 소환
//        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
//        UserService userService = context.getBean(UserService.class);
//        ChannelService channelService = context.getBean(ChannelService.class);
//        MessageService messageService = context.getBean(MessageService.class);
//		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
//		UserStatusService userStatusService = context.getBean(UserStatusService.class);
//
//		UserResponseDto user = setupUser(userService);
//		ChannelResponseDto publicChannel = setupPublicChannel(channelService);
//        ChannelResponseDto privateChannel = setupPrivateChannel(channelService);
//        MessageResponseDto message = messageCreateTest(messageService, publicChannel, user);
//		BinaryContentResponseDto profileImage = binaryContentCreateTest(binaryContentService, user, message);
//		ReadStatusResponseDto readStatus = readStatusCreateTest(readStatusService, publicChannel, user);
//		UserStatusResponseDto userStatus = userStatusCreateTest(userStatusService, user);
//
//		//1. 조회
//		//유저
//		System.out.println("🔍 전체 유저 목록:");
//		System.out.println(userService.findAll());
//		System.out.println("🔍 ID로 유저 조회:");
//		System.out.println(userService.find(user.getId()));
//
//		//채널
//		System.out.println("🔍 전체 메시지 목록:");
//		System.out.println(messageService.findAll());
//		System.out.println("🔍 특정 채널 ID로 메시지 목록 조회:");
//		System.out.println(messageService.findAllByChannelId(publicChannel.getId()));
//		System.out.println("🔍 메시지 ID로 조회:");
//		System.out.println(messageService.find(message.getMessageId()));
//
//	}
	}
}
