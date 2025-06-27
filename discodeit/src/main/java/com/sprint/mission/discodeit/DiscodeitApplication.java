package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.AppConfig;
import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

	public static void main(String[] args) {

		AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		UserService userService = ac.getBean(UserService.class);
		ChannelService channelService = ac.getBean(ChannelService.class);
		MessageService messageService = ac.getBean(MessageService.class);

		//User
		System.out.println("[=============사용자================]");

		//등록
		UserCreateDto userDto1 = new UserCreateDto("에이미", "amy@naver.com", "0987", null, null);
		UserCreateDto userDto2 = new UserCreateDto("밥", "bob@naver.com", "1234", null, null);
		UserCreateDto userDto3 = new UserCreateDto("잭", "jack@naver.com", "1412", null, null);
		UserCreateDto userDto4 = new UserCreateDto("다니엘", "daniel@naver.com", "5432", null, null);

		UserResponseDto u1 = userService.create(userDto1);
		UserResponseDto u2 = userService.create(userDto2);
		UserResponseDto u3 = userService.create(userDto3);
		UserResponseDto u4 = userService.create(userDto4);

		//조회 - 단건
		System.out.println("\n[유저 생성 - 1명 조회]");
		printUserDtos(List.of(userService.findById(u1.getId())));

		//조회 - 다건
		System.out.println("\n[유저 생성 - 전체 조회]");
		printUserDtos(userService.findAll());

		//수정
		System.out.println("\n[사용자 수정 후]");
		UserUpdateDto updateDto = new UserUpdateDto(u1.getId(), "진", "jin@naver.com", "7766", null);
		userService.update(updateDto);
		printUserDtos(List.of(userService.findById(u1.getId())));

		//삭제
		System.out.println("\n[삭제된 유저 제외 출력]");
		userService.delete(u2.getId());
		printUserDtos(userService.findAll());

		//Channel
		System.out.println("\n[=============채널================]");
		/*===============================public 채널===============================*/
		//등록
		ChannelResponseDto public1 = channelService.createPublicChannel(
				new PublicChannelCreateDto("잡담방", "유저 간의 잡담방")
		);
		ChannelResponseDto public2 = channelService.createPublicChannel(
				new PublicChannelCreateDto("통화방", "유저 간의 통화방")
		);

		//조회 - 단건
		System.out.println("\n[Public 채널 생성 - 1개의 채널 조회]");
		printChannelDtos(List.of(channelService.findById(public1.getChannelId(),u1.getId())));

		//조회 - 다건
		System.out.println("\n[Public 전체 채널 탐색]");
		printChannelDtos(channelService.findAllByUserId(u1.getId()));

		// 수정
		System.out.println("\n[Public 수정된 채널]");
		channelService.update(new ChannelUpdateDto(public1.getChannelId(), "수다방", "유저들이 수다떠는 방"));
		printChannelDtos(List.of(channelService.findById(public1.getChannelId(),null)));

		// 삭제
		System.out.println("\n[Public 삭제된 채널 제외 출력]");
		channelService.delete(public2.getChannelId());
		printChannelDtos(channelService.findAllByUserId(u3.getId()));

		/*===============================private 채널===============================*/
		//등록
		List<UUID> participantIds = List.of(u1.getId(), u3.getId(), u4.getId());

		ChannelResponseDto private1 = channelService.createPrivateChannel(
				new PrivateChannelCreateDto(participantIds)
		);

		//조회 - 단건
		System.out.println("\n[Private 채널 생성 - 1개의 채널 조회(user3 기준)]");
		printChannelDtos(List.of(channelService.findById(private1.getChannelId(), u3.getId())));

		//조회 - 다건
		System.out.println("\n[Private 전체 채널 탐색(user1 기준)]");
		printChannelDtos(channelService.findAllByUserId(u1.getId()));

		// 수정
		System.out.println("\n[Private 채널 수정 시도 (예외 발생 예상)]");
		try {
			channelService.update(new ChannelUpdateDto(private1.getChannelId(), "비밀방", "비밀스러운 대화방"));
		} catch (UnsupportedOperationException e) {
			System.out.println("수정 불가: " + e.getMessage());
		}

		// 삭제
		System.out.println("\n[Private 삭제된 채널 제외 출력]");
		channelService.delete(private1.getChannelId());
		printChannelDtos(channelService.findAllByUserId(u1.getId()));

		//Message
		System.out.println("\n[=============메시지================]");

		//등록
		UserCreateDto userDto5 = new UserCreateDto("럼", "rum@naver.com", "8642", null, null);
		UserResponseDto sender = userService.create(userDto5);

		ChannelResponseDto chatChannel = channelService.createPublicChannel(
				new PublicChannelCreateDto("공지방", "공지가 등록될 방입니다."));

		System.out.println("\n[메시지 전송]");
		MessageCreateDto dto1 = new MessageCreateDto(sender.getId(), chatChannel.getChannelId(), "방을 만들었습니다. 잘 보이나요?",  null);
		MessageCreateDto dto2 = new MessageCreateDto(sender.getId(), chatChannel.getChannelId(), "공지방을 만들었습니다. 한번 보시겠어요?", null);
		MessageCreateDto dto3 = new MessageCreateDto(sender.getId(), chatChannel.getChannelId(), "아무말 챌린지", null);
		MessageCreateDto dto4 = new MessageCreateDto(sender.getId(), chatChannel.getChannelId(), "이제 공지는 여기에 올라옵니다.", null);

		MessageResponseDto message1 = messageService.create(dto1);
		MessageResponseDto message2 = messageService.create(dto2);
		MessageResponseDto message3 = messageService.create(dto3);
		MessageResponseDto message4 = messageService.create(dto4);

		//공지방의 모든 메시지 조회
		System.out.println("\n[해당 채널의 메시지 조회]");
		List<MessageResponseDto> messagesInChannel = messageService.findallByChannelId(chatChannel.getChannelId());
		printMessages(sender, messagesInChannel);

		//수정
		System.out.println("\n[수정된 메시지 조회]");
		messageService.update(new MessageUpdateDto(message3.getMessageId(), "엇. 실수로 공지방에 올렸네요.", message3.getUpdatedAt()));
		printMessages(sender,messageService.findallByChannelId(chatChannel.getChannelId()));

		//삭제
		messageService.delete(message1.getMessageId());
		System.out.println("\n[메시지 삭제후]");
		printMessages(sender, messageService.findallByChannelId(chatChannel.getChannelId()));
	}

	private static void printUserDtos(List<UserResponseDto> users) {
		for (UserResponseDto user : users) {
			System.out.printf("ID: %s | 이름: %s | 이메일: %s | 상태: %s (%s)%n",
					user.getId(), user.getUsername(), user.getEmail(),
					user.getUserStatusDto().getStatus(),
					user.getUserStatusDto().getLastActiveAt());
		}
	}

	private static void printChannelDtos(List<ChannelResponseDto> channels) {
		for (ChannelResponseDto channel : channels) {
			System.out.printf("ID: %s | 이름: %s | 설명: %s | 타입: %s | 참여자 수: %d%n",
					channel.getChannelId(),
					channel.getChannelName(),
					channel.getDescription(),
					channel.getChannelType(),
					channel.getParticipantUserIds() != null ? channel.getParticipantUserIds().size() : 0);
		}
	}

	private static void printMessages(UserResponseDto sender, List<MessageResponseDto> messages) {
		if (messages == null || messages.isEmpty()) {
			System.out.printf("사용자 [%s]의 메시지가 없습니다.%n", sender.getUsername());
			return;
		}

		System.out.printf("사용자 [%s]가 작성한 메시지 목록:%n", sender.getUsername());
		for (MessageResponseDto message : messages) {
			System.out.printf("- 내용: %s%n  (작성일시: %s | 수정일시: %s)%n",
					message.getContent(),
					message.getCreatedAt(),
					message.getUpdatedAt());
		}
	}
}
