package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth.LoginUserDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.channel.AllChannelByUserIdResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readStsuts.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class DiscodeitApplication {

	static void userServiceCRUDTest(UserService userService) {
		System.out.println("==============================[ UserService CRUD Test ]==============================");

		// 1) Create
		UserCreateDto createDto = new UserCreateDto(
				"caremanager",
				"caremanager@example.com",
				"password123",
				null
		);
		UserResponseDto created = userService.create(createDto);
		System.out.println("1) Created: " + created);

		// 2) Find by ID
		UUID userId = created.getId();
		UserResponseDto found = userService.find(userId);
		System.out.println("2) Found:   " + found);

		// 3) Update
		UserUpdateDto updateDto = new UserUpdateDto(
				userId,
				"minjeong",
				"minjeong@example.com",
				"newpass456",
				null
		);
		UserResponseDto updated = userService.update(updateDto);
		System.out.println("3) Updated: " + updated);

		// 4) Find All
		System.out.println("4) All Users:");
		userService.findAll().forEach(u -> System.out.println("  " + u));

		// 5) Delete
		userService.delete(userId);
		System.out.println("5) After Delete:");
		userService.findAll().forEach(u -> System.out.println("  " + u));

		System.out.println("==============================================================================");

	}

	static void channelServiceCRUDTest(ChannelService channelService, UserService userService) {
		System.out.println("==========================[ ChannelService CRUD Test ]==========================");

		// --- 준비: 테스트용 유저 두 명 생성 ---
		UserResponseDto user1 = userService.create(new UserCreateDto(
				"minjeong", "minjeong@test.com", "1234", null));
		UserResponseDto user2 = userService.create(new UserCreateDto(
				"minggu",   "minggu@test.com",   "5678", null));

		// 1) PUBLIC 채널 생성
		ChannelCreateDto pubCreate = new ChannelCreateDto(
				ChannelType.PUBLIC,
				"공지", "공지 채널입니다",
				null, null);
		ChannelResponseDto publicChannel = channelService.createPublicChannel(pubCreate);
		System.out.println("1) Created PUBLIC Channel: " + publicChannel);

		// 2) PUBLIC 조회
		ChannelResponseDto pubFound = channelService.find(publicChannel.getId(), user1.getId());
		System.out.println("2) Found PUBLIC:   " + pubFound);

		// 3) PUBLIC 수정
		ChannelUpdateDto pubUpdate = new ChannelUpdateDto(
				publicChannel.getId(),
				ChannelType.PUBLIC,
				"안내", "공지 채널에서 '안내'채널로 수정되었습니다.");
		ChannelResponseDto pubUpd = channelService.update(pubUpdate);
		System.out.println("3) Updated PUBLIC: " + pubUpd);

		// 4) findAllByUserId → public only
		System.out.println("4) All PUBLIC channels for user1:");
		channelService.findAllByUserId(user1.getId())
				.getPublicChannels()
				.forEach(c -> System.out.println("   " + c));

		// 5) PRIVATE 채널 생성 (user1 ↔ user2)
		ChannelCreateDto privCreate = new ChannelCreateDto(
				ChannelType.PRIVATE,
				null, null,
				user1.getId(), user2.getId());
		ChannelResponseDto priv = channelService.createPrivateChannel(privCreate);
		System.out.println("5) Created PRIVATE: " + priv);

		// 6) PRIVATE 조회 (각각 관점)
		System.out.println("6-1) Found PRIVATE as user1: " +
				channelService.find(priv.getId(), user1.getId()));
		System.out.println("6-2) Found PRIVATE as user2: " +
				channelService.find(priv.getId(), user2.getId()));

		// 7) findAllByUserId → private only
		System.out.println("7) All PRIVATE channels for u1:");
		channelService.findAllByUserId(user1.getId())
				.getPrivateChannels()
				.forEach(c -> System.out.println("   " + c));

		// 8) 삭제
		channelService.delete(publicChannel.getId());
		channelService.delete(priv.getId());
		System.out.println("8) After Delete, all channels for u1:");
		AllChannelByUserIdResponseDto after = channelService.findAllByUserId(user1.getId());
		System.out.println("   PUBLIC:");   after.getPublicChannels().forEach(c -> System.out.println("     " + c));
		System.out.println("   PRIVATE:");  after.getPrivateChannels().forEach(c -> System.out.println("     " + c));

		System.out.println("==============================================================================");
	}

	static void messageServiceCRUDTest(MessageService messageService,
									   ChannelService channelService,
									   UserService userService) {
		System.out.println("=========================[ MessageService CRUD Test ]=========================");

		// --- 준비: 테스트용 유저와 PUBLIC 채널 생성 ---
		UserResponseDto user = userService.create(new UserCreateDto(
				"alice", "alice@test.com", "1234", null));
		ChannelResponseDto channel = channelService.createPublicChannel(new ChannelCreateDto(
				ChannelType.PUBLIC,
				"메시지 테스트", "메시지 테스트용 채널",
				null, null));
		UUID channelId = channel.getId();

		// 1) Create
		MessageCreateDto createDto = new MessageCreateDto(
				"메시지",
				channelId,
				user.getId(),
				List.of()   // 첨부파일 없음
		);
		MessageResponseDto created = messageService.create(createDto);
		System.out.println("1) Created: " + created);

		// 2) Find by ID
		UUID msgId = created.getId();
		MessageResponseDto found = messageService.find(msgId);
		System.out.println("2) Found:   " + found);

		// 3) Update
		MessageUpdateDto updateDto = new MessageUpdateDto(
				msgId,
				"수정된 메시지",
				List.of()  // 첨부파일 변경 없음
		);
		MessageResponseDto updated = messageService.update(updateDto);
		System.out.println("3) Updated: " + updated);

		// 4) findAllByChannelId
		System.out.println("4) All messages in channel:");
		messageService.findAllByChannelId(channelId)
				.forEach(m -> System.out.println("   " + m));

		// 5) Delete
		messageService.delete(msgId);
		System.out.println("5) After Delete, messages in channel:");
		messageService.findAllByChannelId(channelId)
				.forEach(m -> System.out.println("   " + m));

		System.out.println("==============================================================================");
	}

	static void authServiceTest(AuthService authService, UserService userService) {
		System.out.println("=============================[ AuthService Test ]=============================");

		// --- 준비: 테스트용 유저 생성 ---
		UserCreateDto userDto = new UserCreateDto(
				"bob", "bob@test.com", "1q2w3e4r", null
		);
		UserResponseDto user = userService.create(userDto);
		System.out.println("0) User created: " + user);

		// 1) 정상 로그인
		LoginUserDto loginDto = new LoginUserDto(user.getUsername(), "1q2w3e4r");
		UserResponseDto loggedIn = authService.login(loginDto);
		System.out.println("1) Login success: " + loggedIn);

		// 2) 비밀번호 틀린 로그인
		try {
			authService.login(new LoginUserDto(user.getUsername(), "wrongpassword"));
		} catch (IllegalArgumentException e) {
			System.out.println("2) Login failed (wrong password): " + e.getMessage());
		}

		// 3) 존재하지 않는 유저 로그인
		try {
			authService.login(new LoginUserDto("merrong", "12345678"));
		} catch (IllegalArgumentException e) {
			System.out.println("3) Login failed (no such user): " + e.getMessage());
		}

		System.out.println("==============================================================================");
	}

	static void binaryContentServiceCRUDTest(BinaryContentService binaryContentService) {
		System.out.println("======================[ BinaryContentService CRUD Test ]======================");

		// 1) Create
		byte[] data = new byte[]{10, 20, 30};
		BinaryContentCreateDto createDto = new BinaryContentCreateDto(data, BinaryContentType.PROFILE);
		BinaryContentResponseDto created = binaryContentService.create(createDto);
		System.out.println("1) Created: " + created);

		// 2) Find by ID
		UUID id = created.getId();
		BinaryContentResponseDto found = binaryContentService.find(id);
		System.out.println("2) Found:   " + found);

		// 3) findAllByIdIn
		List<UUID> ids = List.of(id);
		List<BinaryContentResponseDto> list = binaryContentService.findAllByIdIn(ids);
		System.out.println("3) findAllByIdIn: " + list);

		// 4) Delete
		binaryContentService.delete(id);
		try {
			binaryContentService.findAllByIdIn(List.of(id));
			System.out.println("=> (X) Expected exception, but got none");
		} catch (NoSuchElementException e) {
			System.out.println("=> (O) Got expected exception on find after delete: " + e.getMessage());
		}

		System.out.println("==============================================================================");
	}

	static void readStatusServiceCRUDTest(ReadStatusService readStatusService, UserService userService, ChannelService channelService) {
		System.out.println("=======================[ ReadStatusService CRUD Test ]========================");

		// --- 준비: 테스트용 유저와 채널 생성 ---
		UserResponseDto user = userService.create(new UserCreateDto(
				"celery", "celery@test.com", "pw", null));
		ChannelResponseDto channel = channelService.createPublicChannel(new ChannelCreateDto(
				ChannelType.PUBLIC,
				"읽기상태테스트", "ReadStatus 테스트용 채널",
				null, null));
		UUID userId = user.getId();
		UUID channelId = channel.getId();

		// 1) Create
		ReadStatusCreateDto createDto = new ReadStatusCreateDto(userId, channelId);
		ReadStatusResponseDto created = readStatusService.create(createDto);
		System.out.println("1) Created: " + created);

		// 1-1) 중복 생성 시도 -> IllegalArgumentException
		try {
			readStatusService.create(createDto);
			System.out.println("   (X) Expected exception on duplicate create, but none");
		} catch (IllegalArgumentException e) {
			System.out.println("   (O) Duplicate create failed as expected: " + e.getMessage());
		}

		// 2) Find by ID
		ReadStatusResponseDto found = readStatusService.find(created.getId());
		System.out.println("2) Found by id:   " + found);

		// 3) findAllByUserId
		System.out.println("3) All for user:");
		readStatusService.findAllByUserId(userId)
				.forEach(rs -> System.out.println("   " + rs));

		// 4) Update (touch lastReadAt)
		ReadStatusUpdateDto updateDto = new ReadStatusUpdateDto(created.getId());
		ReadStatusResponseDto updated = readStatusService.update(updateDto);
		System.out.println("4) Updated (lastReadAt refreshed): " + updated);

		// 5) Delete
		readStatusService.delete(created.getId());
		System.out.println("5) After Delete, all for user:");
		readStatusService.findAllByUserId(userId)
				.forEach(rs -> System.out.println("   " + rs));

		System.out.println("==============================================================================");
	}

	static void userStatusServiceCRUDTest(UserStatusService userStatusService,
										  UserService userService) {
		System.out.println("========================[ UserStatusService CRUD Test ]========================");

		// --- 준비: 테스트용 유저 생성 ---
		UserResponseDto user = userService.create(new UserCreateDto(
				"delivery", "delivery@test.com", "pw", null));
		UUID userId = user.getId();

		// 1) Create (유저 생성 시 자동 생성됨)
		UserStatusResponseDto created = userStatusService.findAll().stream()
				.filter(us -> us.getUserId().equals(user.getId()))
				.findFirst()
				.orElseThrow();
		System.out.println("1) Created: " + created);

		// 1-1) 중복 생성 시도 → IllegalArgumentException
		try {
			UserStatusCreateDto createDto = new UserStatusCreateDto(userId);
			userStatusService.create(createDto);
			System.out.println("   (X) Expected exception on duplicate create, but none");
		} catch (IllegalArgumentException e) {
			System.out.println("   (O) Duplicate create failed as expected: " + e.getMessage());
		}

		// 2) Find by ID
		UserStatusResponseDto found = userStatusService.find(created.getId());
		System.out.println("2) Found by id: " + found);

		// 3) Find all (서비스에 findAll 있으면; 기본 구현은 findAll())
		System.out.println("3) All statuses:");
		userStatusService.findAll().forEach(us -> System.out.println("   " + us));

		// 4) UpdateByUserId (lastConnectedAt 갱신)
		UserStatusResponseDto updatedByUser = userStatusService.updateByUserId(userId);
		System.out.println("4) Updated byUser:  " + updatedByUser);

		// 5) Update by ID (touch만 되므로 동일 ID로)
		UserStatusUpdateDto updateDto = new UserStatusUpdateDto(found.getId());
		UserStatusResponseDto updated = userStatusService.update(updateDto);
		System.out.println("5) Updated byId:    " + updated);

		// 6) Delete
		userStatusService.delete(found.getId());
		System.out.println("6) After Delete, all statuses:");
		userStatusService.findAll().forEach(us -> System.out.println("   " + us));

		System.out.println("==============================================================================");
	}

	private static void clearDataDirectory() {
		Path dataDir = Paths.get("data/discodeit");
		if (Files.exists(dataDir)) {
			try (Stream<Path> paths = Files.walk(dataDir)) {
				paths
						.sorted(Comparator.reverseOrder())  // 파일 먼저, 디렉터리 나중
						.forEach(p -> {
							try {
								Files.deleteIfExists(p);
								System.out.println("파일 삭제 완료: " + p);
							} catch (IOException e) {
								// 필요에 따라 로깅
								System.err.println("파일 삭제 실패: " + p + " – " + e.getMessage());
							}
						});
			} catch (IOException e) {
				throw new UncheckedIOException("data 디렉터리 초기화 실패", e);
			}
		}
	}


	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		// ===============================[ 서비스 초기화 ]===============================
		ChannelService channelService = context.getBean(ChannelService.class);
		UserService userService = context.getBean(UserService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);

		// ===================[ 서비스 테스트 준비 (File 기반 구현체에 한정]===================

		/**
		 * File 기반 구현체의 경우 clearDataDirectory 메서드를 실행시켜 기존 데이터를 지워줘야 합니다.
		 * 아래 서비스 테스트와는 독립적으로 실행시켜주어야 합니다.
		 */
//		clearDataDirectory();

		// ===============================[ 서비스 테스트 ]===============================

		userServiceCRUDTest(userService);
		channelServiceCRUDTest(channelService, userService);
        messageServiceCRUDTest(messageService, channelService, userService);
        authServiceTest(authService, userService);
        binaryContentServiceCRUDTest(binaryContentService);
        readStatusServiceCRUDTest(readStatusService, userService, channelService);
		userStatusServiceCRUDTest(userStatusService, userService);
	}
}
