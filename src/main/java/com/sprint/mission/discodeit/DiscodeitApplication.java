package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.auth_service_dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.BinaryContentsResponseDto;
import com.sprint.mission.discodeit.dto.binary_contents_dto.CreateBinaryContentsRequestDto;
import com.sprint.mission.discodeit.dto.channel_service_dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel_service_dto.CreateChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel_service_dto.DeleteChannelRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.DeleteMessageRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message_service_dto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.CreateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.DeleteReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus_dto.UpdateReadStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_service_dto.*;
import com.sprint.mission.discodeit.dto.user_status_dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.user_status_dto.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.repository.BinaryContentsRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.BasicAuthService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {
	public static final String pathStaticFolder = "./src/main/resources/static/";
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
		mainTest(context);
	}

	public static void mainTest(ConfigurableApplicationContext context) {

		deleteAllFilesInDataFolder();
		//userTest(context); //유저 서비스 테스트및 채널 서비스에서 사용할 유저 생성
		//channelTest(context);
		//messageTest(context);
		//readStatusTest(context);
		//userStatusTest(context);
		binaryContentTest(context);
	}

	public static void userTest(ConfigurableApplicationContext context) {

		deleteAllFilesInDataFolder();

		UserService userService = context.getBean(UserService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentsRepository binaryContentsRepository = context.getBean(BinaryContentsRepository.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);

		System.out.println("\n========== userService Test start ==========================================\n");
		System.out.println("1. 유저 등록=================");

		String profilePicture1Path =  pathStaticFolder + "/basicUserProfileImage.png"; // 기본 이미지, 유저가 선택하면 여기 값이 바뀌도록 해서 기본 이미지가 있도록 유지

		UserCreateRequestDto user1Dto = new UserCreateRequestDto("kwon1", "pw1",  "kwon1@email.com", profilePicture1Path);
		UserCreateRequestDto user2Dto = new UserCreateRequestDto("kwon2", "pw2",  "kwon2@email.com", null); // null은 사진을 선택하지 않았다는 의미

		UserResponseDto user1 = userService.createUser(user1Dto);
		UserResponseDto user2 = userService.createUser(user2Dto);

		AuthService basicAuthService = context.getBean(BasicAuthService.class);
		LoginRequestDto loginRequestDTO = new LoginRequestDto("kwon1", "pw1");
		UserResponseDto loginUser1 = basicAuthService.logInUser(loginRequestDTO);
		System.out.println(loginUser1.getUserName() + "님이 로그인에 성공했습니다.");

		/*
		System.out.println(user2.getProfileId());
		binaryContentsRepository = context.getBean(BinaryContentsRepository.class);
		BinaryContents user1ProfileImg = binaryContentsRepository.getBinaryContentsById(user1.getProfileId());
		testImgBySave(user1ProfileImg.getBinaryData()); // 이미지 제대로 저장되었는지 확인*/


		List<UserStatusResponseDto> userStatusResponseDtos = userStatusService.findAllUserStatus();
		System.out.println("=== 전체 유저 로그인 상태 ===");
		userStatusResponseDtos.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();

		System.out.println("=== 단일 유저 로그인 상태 ===");
		UserStatusResponseDto userStatusResponseDTO = userStatusService.findUserStatusByUserId(user1.getUserId());
		System.out.println("사용자: " + userStatusResponseDTO.getUserName() + " | 상태: " + userStatusResponseDTO.getIsLoggedIn());
		System.out.println();

		System.out.println("=== 유저 업데이트 ===");
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto(loginUser1.getUserId(), "newKwon1", "newPw1", "newEMail", ".\\src\\main\\resources\\static\\basicUserProfileImage2.png");
		userService.updateUser(userUpdateRequestDto);
		userStatusResponseDTO = userStatusService.findUserStatusByUserId(user1.getUserId());
		System.out.println("사용자: " + userStatusResponseDTO.getUserName() + " | 상태: " + userStatusResponseDTO.getIsLoggedIn());
		System.out.println();

		System.out.println("=== 전체 유저 로그인 상태 ===");
		userStatusResponseDtos = userStatusService.findAllUserStatus();
		userStatusResponseDtos.forEach(userLoginData -> System.out.println("사용자: " + userLoginData.getUserName() + " | 상태: " + userLoginData.getIsLoggedIn()));
		System.out.println();

		System.out.println("=== 유저 삭제 전 ActiveUserDTo 확인 ===");
		List<UserResponseDto> acitveUserResponseDto = userService.findAllActiveUserDTO();
		printUserDTOs(acitveUserResponseDto);
		System.out.println();

		System.out.println("=== 유저 삭제 전 DeactiveUserDTO 확인 ===");
		List<UserResponseDto> deacitveUserResponseDto = userService.findAllDeactiveUserDTO();
		printUserDTOs(deacitveUserResponseDto);
		System.out.println();

		/*
		userService.deleteUser(user1);

		System.out.println("=== 유저 삭제 후 ActiveUserDTo 확인 ===");
		AcitveUserDTO = userService.findAllActiveUserDTO();
		printUserDTOs(AcitveUserDTO);
		System.out.println();

		System.out.println("=== 유저 삭제 후 DeactiveUserDTO 확인 ===");
		DeacitveUserDTO = userService.findAllDeactiveUserDTO();
		printUserDTOs(DeacitveUserDTO);
		System.out.println();*/
	}

	public static void channelTest(ConfigurableApplicationContext context) {
		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);

		LoginRequestDto loginRequestDTO = new LoginRequestDto("newKwon1", "pw1");
		LoginRequestDto loginRequestDto2 = new LoginRequestDto("kwon2", "pw2");

		UserResponseDto user1 = authService.logInUser(loginRequestDTO);
		UserResponseDto user2 = authService.logInUser(loginRequestDto2);

		System.out.println("\n=== Public 채널 생성===");
		CreateChannelRequestDto createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널", "권용진의 1의 public 채널입니다.");
		ChannelResponseDto channel1 = channelService.createPublicChannel(createChannelRequestDTO);

		createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널2", "권용진의 1의 public 채널2 입니다.");
		ChannelResponseDto channel2 = channelService.createPublicChannel(createChannelRequestDTO);
		System.out.println("\n=== Private 채널 생성===");
		createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 private 채널", "권용진의 1의 private 채널입니다.");
		ChannelResponseDto channel3 = channelService.createPrivateChannel(createChannelRequestDTO, user2);

		System.out.println("\n=== 모든 Public 채널 출력 ===");
		List<ChannelResponseDto> allPublicChannel = channelService.findPublicChannel();
		printChannelDTOs(allPublicChannel);

		System.out.println("\n=== user1의 모든 Private 채널 출력 ===");
		List<ChannelResponseDto> allPrivateChannel = channelService.findPrivateChannel(user1);
		printChannelDTOs(allPrivateChannel);

		System.out.println("\n=== channel1에 메시지 추가===");
		MessageCreateRequestDto messageCreateRequestDTO = new MessageCreateRequestDto(user1, channel1, "권용진1의 채널1에서 새로운 메세지");
		messageService.createMessage(messageCreateRequestDTO); // 메세지가 계속 추가되서 잠시 주석처리
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== channel1의 마지막 메시지 Id 출력 ===");
		createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널", "권용진의 1의 public 채널입니다.");
		channel1 = channelService.findChannelDTOByCannelId(channel1.getChannelId()); // 메세지를 더했으니까 채널 수동 업데이트
		System.out.println(channel1.getLastMessageId());
		System.out.println("channel1의 메세지 개수 " + channel1.getMessageIds().size());

		/*
		System.out.println("\n*** channel1의 이름 변경 ***");
		System.out.println("변경 전 이름: " + channel1.getChannelName());
		ChannelNameUpdateRequestDTO channelNameUpdateRequestDTO = new ChannelNameUpdateRequestDTO(user1, channel1, "새로운 이름의 채널1");
		channelService.updateChannelName(channelNameUpdateRequestDTO);
		channel1 = channelService.findChannelDTOByCannelId(channel1.getChannelId());
		System.out.println("변경 후 이름: " + channel1.getChannelName());

		System.out.println("\n*** channel1의 호스트 변경 ***");
		System.out.println("변경 전 호스트의 ID: " + channel1.getHostUserId());
		ChannelHostUserUpdateRequestDTO channelHostUserUpdateRequestDTO = new ChannelHostUserUpdateRequestDTO(user1, user2, channel1);
		channelService.addUserToChannel(new AddUserToChannelRequestDTO(user2, channel1));

		channel1 = channelService.findChannelDTOByCannelId(channel1.getChannelId());

		channelService.updateHostUser(channelHostUserUpdateRequestDTO);
		channel1 = channelService.findChannelDTOByCannelId(channel1.getChannelId());
		System.out.println("변경 후 호스트의 ID: " + channel1.getHostUserId());*/ //채널 삭제 테스트를 위한 주석

		System.out.println("\n*** 채널 삭제 ***");
		System.out.println("\n=== 삭제 전 모든 Public 채널 출력 ===");
		allPublicChannel = channelService.findPublicChannel();
		printChannelDTOs(allPublicChannel);

		System.out.println("\n=== 삭제 전 모든 Message 출력 ===");
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== 삭제 전 모든 ReadStatus 출력 ===");
		channelService.findAllReadStatus().stream().forEach(readStatus -> System.out.println("유저 ID: " + readStatus.getUserId() + "채널 ID: " + readStatus.getChannelId()));

		channelService.deleteChannel(new DeleteChannelRequestDto(user1, channel1));

		System.out.println("\n=== 삭제 후 모든 Public 채널 출력 ===");
		allPublicChannel = channelService.findPublicChannel();
		printChannelDTOs(allPublicChannel);

		System.out.println("\n=== 삭제 후 모든 Message 출력 ===");
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== 삭제 후 모든 ReadStatus 출력 ===");
		channelService.findAllReadStatus().stream().forEach(readStatus -> System.out.println("유저 ID: " + readStatus.getUserId() + "채널 ID: " + readStatus.getChannelId()));
	}

	public static void messageTest(ConfigurableApplicationContext context) {

		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		BinaryContentsService binaryContentsService = context.getBean(BinaryContentsService.class);

		LoginRequestDto loginRequestDTO = new LoginRequestDto("newKwon1", "pw1");
		LoginRequestDto loginRequestDto2 = new LoginRequestDto("kwon2", "pw2");

		UserResponseDto user1 = authService.logInUser(loginRequestDTO);
		UserResponseDto user2 = authService.logInUser(loginRequestDto2);

		System.out.println("\n=== Public 채널 생성===");
		CreateChannelRequestDto createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널", "권용진의 1의 public 채널입니다.");
		ChannelResponseDto channel1 = channelService.createPublicChannel(createChannelRequestDTO);

		createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널2", "권용진의 1의 public 채널2 입니다.");
		ChannelResponseDto channel2 = channelService.createPublicChannel(createChannelRequestDTO);

		System.out.println("\n=== channel1,2 에 메시지 추가===");
		List<String> extraFilesPath = new ArrayList<>();
		extraFilesPath.add(pathStaticFolder + "messageExtraFileTest1.txt");
		extraFilesPath.add(pathStaticFolder + "basicUserProfileImage.png");
		// 추가 첨부 파일을 선택했다는 의미

		MessageCreateRequestDto messageCreateRequestDTO = new MessageCreateRequestDto(user1, channel1, "권용진1의 채널1에서 새로운 메세지", extraFilesPath);
		MessageResponseDto messageResponseDto1 = messageService.createMessage(messageCreateRequestDTO); // 메세지가 계속 추가되서 잠시 주석처리

		messageCreateRequestDTO = new MessageCreateRequestDto(user1, channel2, "권용진1의 채널2에서 새로운 메세지", extraFilesPath);
		MessageResponseDto messageResponseDto2 = messageService.createMessage(messageCreateRequestDTO); // 메세지가 계속 추가되서 잠시 주석처리

		System.out.println("\n=== 전체 메시지 출력===");
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== channel1 메시지 출력===");
		printMessageDTOs(messageService.findMessagesByChannelId(channel1.getChannelId()));

		System.out.println("\n=== channel2 메시지 출력===");
		printMessageDTOs(messageService.findMessagesByChannelId(channel2.getChannelId()));


		System.out.println("\n=== 메시지1 업데이트===");
		MessageUpdateRequestDto messageUpdateRequestDto1 = new MessageUpdateRequestDto(user1, messageResponseDto1, "새로운 메시지 입니다.", null);
		messageService.updateMessage(messageUpdateRequestDto1);

		System.out.println("\n=== 메시지 삭제 전 전체 메시지 출력===");
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== 메시지 삭제 전 전체 바이너리 출력 출력===");
		printBinaryContentsResponseDTOs(binaryContentsService.findAllBinaryContentsDtos());

		System.out.println("\n=== 메시지2 삭제===");
		DeleteMessageRequestDto deleteMessageRequestDTO = new DeleteMessageRequestDto(user1, messageResponseDto2);
		messageService.deleteMessage(deleteMessageRequestDTO);

		System.out.println("\n=== 메시지 삭제 후 전체 메시지 출력===");
		printMessageDTOs(messageService.findAllMessage());

		System.out.println("\n=== 메시지 삭제 후 전체 바이너리 출력 출력===");
		printBinaryContentsResponseDTOs(binaryContentsService.findAllBinaryContentsDtos());
		// 정상적으로 수행 됨
	}

	public static void readStatusTest(ConfigurableApplicationContext context) {
		AuthService authService = context.getBean(AuthService.class);
		ChannelService channelService = context.getBean(ChannelService.class);
		MessageService messageService = context.getBean(MessageService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		BinaryContentsService binaryContentsService = context.getBean(BinaryContentsService.class);

		LoginRequestDto loginRequestDTO = new LoginRequestDto("newKwon1", "pw1");
		LoginRequestDto loginRequestDTO2 = new LoginRequestDto("kwon2", "pw2");

		UserResponseDto user1 = authService.logInUser(loginRequestDTO);
		UserResponseDto user2 = authService.logInUser(loginRequestDTO2);

		System.out.println("\n=== Public 채널 생성===");
		CreateChannelRequestDto createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널", "권용진의 1의 public 채널입니다.");
		ChannelResponseDto channel1 = channelService.createPublicChannel(createChannelRequestDTO);

		createChannelRequestDTO = new CreateChannelRequestDto(user1.getUserId(), "권용진1의 public 채널2", "권용진의 1의 public 채널2 입니다.");
		ChannelResponseDto channel2 = channelService.createPublicChannel(createChannelRequestDTO);

		// 추가 첨부 파일을 선택했다는 의미

		System.out.println("\n=== ReadStatus 추가===");
		CreateReadStatusRequestDto createReadStatusRequestDto = new CreateReadStatusRequestDto(user1, channel1);
		ReadStatusResponseDto readStatusResponseDto = readStatusService.createReadStatus(createReadStatusRequestDto);

		System.out.println("\n=== ReadStatus 전체 출력===");
		List<ReadStatusResponseDto> readStatusResponseDtos = readStatusService.findAllReadStatus();
		printReadStatusResponseDTOs(readStatusResponseDtos);

		System.out.println("\n=== ReadStatus 유저1 출력===");
		readStatusResponseDtos = readStatusService.findReadStatusByUserId(user1.getUserId());
		printReadStatusResponseDTOs(readStatusResponseDtos);

		System.out.println("\n=== ReadStatus 채널1 출력===");
		readStatusResponseDtos = readStatusService.findReadStatusByChannelId(channel1.getChannelId());
		printReadStatusResponseDTOs(readStatusResponseDtos);

		System.out.println("\n=== ReadStatus 업데이트 후 출력===");
		UpdateReadStatusRequestDto updateReadStatusRequestDto = new UpdateReadStatusRequestDto(readStatusResponseDto.getUserId(), readStatusResponseDto.getChannelId(), readStatusResponseDto.getReadStatusId());
		readStatusResponseDto = readStatusService.updateReadStatus(updateReadStatusRequestDto);
		readStatusResponseDtos = readStatusService.findAllReadStatus();
		printReadStatusResponseDTOs(readStatusResponseDtos);

		System.out.println("\n=== ReadStatus 삭제 후 출력===");
		DeleteReadStatusRequestDto deleteReadStatusRequestDto = new DeleteReadStatusRequestDto(readStatusResponseDto.getUserId(), readStatusResponseDto.getChannelId(), readStatusResponseDto.getReadStatusId());
		readStatusService.deleteReadStatus(deleteReadStatusRequestDto);
		readStatusResponseDtos = readStatusService.findAllReadStatus();
		printReadStatusResponseDTOs(readStatusResponseDtos);
	}

	public static void userStatusTest(ConfigurableApplicationContext context) {
		AuthService authService = context.getBean(AuthService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);
		/*
		UserService userService = context.getBean(UserService.class);
		UserCreateRequestDto user1Dto = new UserCreateRequestDto("kwon1", "pw1",  "kwon1@email.com", null);
		UserCreateRequestDto user2Dto = new UserCreateRequestDto("kwon2", "pw2",  "kwon2@email.com", null); // null은 사진을 선택하지 않았다는 의미

		UserResponseDto user1 = userService.createUser(user1Dto);
		UserResponseDto user2 = userService.createUser(user2Dto);*/

		LoginRequestDto loginRequestDto = new LoginRequestDto("kwon1", "pw1");
		LoginRequestDto loginRequestDto2 = new LoginRequestDto("kwon2", "pw2");

		UserResponseDto user1 = authService.logInUser(loginRequestDto);
		UserResponseDto user2 = authService.logInUser(loginRequestDto2);

		System.out.println("=== 전체 userStatus출력 ===");

		CreateUserStatusRequestDto createUserStatusRequestDto = new CreateUserStatusRequestDto(user1.getUserId());
		UserStatusResponseDto user1Status = userStatusService.createUserStatus(createUserStatusRequestDto);

		createUserStatusRequestDto = new CreateUserStatusRequestDto(user2.getUserId());
		UserStatusResponseDto user2Status = userStatusService.createUserStatus(createUserStatusRequestDto);

		List<UserStatusResponseDto> userStatusList = userStatusService.findAllUserStatus();
		printUserStatusResponseDTOs(userStatusList);

		System.out.println("=== User1 userStatus 출력 ===");
		user1Status = userStatusService.findUserStatusByUserId(user1.getUserId());
		System.out.println("UserStatus ID: " + user1Status.getUserStatusId() + ", User ID: " + user1Status.getUserId() + ", Time: " + user1Status.getLoginTime());

		System.out.println("=== User1 userStatus 업데이트 ===");
		UpdateUserStatusRequestDto updateUserStatusRequestDto = new UpdateUserStatusRequestDto(user1Status.getUserId(), user1Status.getUserStatusId());
		user1Status = userStatusService.updateUserStatus(updateUserStatusRequestDto);
		System.out.println("UserStatus ID: " + user1Status.getUserStatusId() + ", User ID: " + user1Status.getUserId() + ", Time: " + user1Status.getLoginTime());

		System.out.println("=== User1 userStatus 삭제 ===");
		userStatusService.deleteUserStatusByUserId(user1Status.getUserId());
		userStatusList = userStatusService.findAllUserStatus();
		printUserStatusResponseDTOs(userStatusList);
	}

	public static void binaryContentTest(ConfigurableApplicationContext context) {
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentsService binaryContentsService = context.getBean(BinaryContentsService.class);
		UserService userService = context.getBean(UserService.class);

		UserCreateRequestDto user1Dto = new UserCreateRequestDto("kwon1", "pw1",  "kwon1@email.com", null);
		UserCreateRequestDto user2Dto = new UserCreateRequestDto("kwon2", "pw2",  "kwon2@email.com", null); // null은 사진을 선택하지 않았다는 의미

		UserResponseDto user1 = userService.createUser(user1Dto);
		UserResponseDto user2 = userService.createUser(user2Dto);

		LoginRequestDto loginRequestDto = new LoginRequestDto("kwon1", "pw1");
		LoginRequestDto loginRequestDto2 = new LoginRequestDto("kwon2", "pw2");

		//UserResponseDto user1 = authService.logInUser(loginRequestDto);
		//UserResponseDto user2 = authService.logInUser(loginRequestDto2);
		String profilePicture1Path =  pathStaticFolder + "/basicUserProfileImage.png"; // 기본 이미지, 유저가 선택하면 여기 값이 바뀌도록 해서 기본 이미지가 있도록 유지

		System.out.println("=== 바이너리 컨텐츠 추가 및 전체 출력 ===");
		CreateBinaryContentsRequestDto createBinaryContentsRequestDto = new CreateBinaryContentsRequestDto(user1.getUserId(), profilePicture1Path, BinaryContentType.USER_PROFILE_IMAGE);
		BinaryContentsResponseDto binaryContentsResponseDto = binaryContentsService.createBinaryContents(createBinaryContentsRequestDto);

		List<BinaryContentsResponseDto> binaryContentsResponseDtos = binaryContentsService.findAllBinaryContentsDtos();
		printBinaryContentsResponseDTOs(binaryContentsResponseDtos);

		System.out.println("=== reference id로 전체 출력 ===");
		binaryContentsResponseDtos = binaryContentsService.findBinaryContentsDtosByReferenceId(binaryContentsResponseDto.getReferenceId());
		printBinaryContentsResponseDTOs(binaryContentsResponseDtos);

		System.out.println("=== binaryContents id로 출력 ===");
		System.out.println("contentsId: " + binaryContentsService.findBinaryContentsDTOByBinaryContentsId( binaryContentsResponseDto.getBinaryContentsId()).getBinaryContentsId() + ", reference Id: " + binaryContentsService.findBinaryContentsDTOByBinaryContentsId(binaryContentsResponseDto.getBinaryContentsId()).getReferenceId());

		System.out.println("=== 삭제 후 전체 출력 ===");
		binaryContentsService.deleteBinaryContentsDTOById(binaryContentsResponseDto.getBinaryContentsId());
		binaryContentsResponseDtos = binaryContentsService.findBinaryContentsDtosByReferenceId(binaryContentsResponseDto.getReferenceId());
		printBinaryContentsResponseDTOs(binaryContentsResponseDtos);
	}

	public static void printUserDTOs(List<UserResponseDto> userResponseDtos) {
		//TODO 테스트 코드 추가하기
		for (UserResponseDto userResponseDto : userResponseDtos) {
			System.out.println("유저 id: " + userResponseDto.getUserId() + ", 이름: " + userResponseDto.getUserName());
		}
	}

	public static void printChannelDTOs(List<ChannelResponseDto> channelResponseDtos) {
		for (ChannelResponseDto dto : channelResponseDtos) {
			StringBuilder sb = new StringBuilder();

			boolean isPrivate = dto.getChannelType() == ChannelType.PRIVATE_CHANNEL;
			sb.append(isPrivate ? "Private Channel: " : "Public Channel: ");
			sb.append(dto.getChannelId());
			sb.append(", 유저 수: ").append(dto.getUserIds().size());

			if (isPrivate) {
				sb.append(", 참여 User Id: ").append(dto.getUserIds());
			}

			System.out.println(sb);
		}
	}

	public static void printMessageDTOs(List<MessageResponseDto> messageResponseDtos) {
		for (MessageResponseDto dto : messageResponseDtos) {
			StringBuilder sb = new StringBuilder();
			sb.append("메세지 id: ");
			sb.append(dto.getMessageId());
			sb.append(", 채널 id: ");
			sb.append(dto.getChannelId());
			sb.append(", 유저 id: ");
			sb.append(dto.getAuthorId());

			sb.append(", 내용 id: ");
			sb.append(dto.getMessageContents());

			sb.append(", ");

			if (dto.getBinaryContentIds() != null) {
				sb.append("바이너리 ID: ").append(dto.getBinaryContentIds());
			}

			System.out.println(sb);
		}
	}

	public static void printReadStatusResponseDTOs(List<ReadStatusResponseDto> dtos) {
		if (dtos.isEmpty()) {
			System.out.println("DTO가 비었습니다.");
			return;
		}

		for (ReadStatusResponseDto dto : dtos) {
			StringBuilder sb = new StringBuilder();

			sb.append("ReadStatus ID: ");
			sb.append(dto.getReadStatusId());
			sb.append(", User ID: ");
			sb.append(dto.getUserId());
			sb.append(", Channel ID: ");
			sb.append(dto.getChannelId());
			sb.append(", Time: ");
			sb.append(dto.getReadTime());


			System.out.println(sb);
		}
	}

	public static void printUserStatusResponseDTOs(List<UserStatusResponseDto> dtos) {
		if (dtos.isEmpty()) {
			System.out.println("DTO가 비었습니다.");
			return;
		}

		for (UserStatusResponseDto dto : dtos) {
			StringBuilder sb = new StringBuilder();

			sb.append("UserStatus ID: ");
			sb.append(dto.getUserStatusId());
			sb.append(", User ID: ");
			sb.append(dto.getUserId());
			sb.append(", Time: ");
			sb.append(dto.getLoginTime());


			System.out.println(sb);
		}
	}

	public static void printBinaryContentsResponseDTOs(List<BinaryContentsResponseDto> dtos) {
		if (dtos.isEmpty()) {
			System.out.println("DTO가 비었습니다.");
			return;
		}

		for (BinaryContentsResponseDto dto : dtos) {
			StringBuilder sb = new StringBuilder();

			sb.append("BinaryContents ID: ");
			sb.append(dto.getBinaryContentsId());
			sb.append(", Reference ID: ");
			sb.append(dto.getReferenceId());
			sb.append(", Type: ");
			sb.append(dto.getBinaryContentType());


			System.out.println(sb);
		}
	}


	public static void testImgBySave(byte[] imageBytes){

		File outputFile = new File(pathStaticFolder + "/restored_user_profile.jpg"); // 저장될 파일명
		try (FileOutputStream fos = new FileOutputStream(outputFile)) {
			fos.write(imageBytes);
			System.out.println("이미지 파일로 저장 완료: " + outputFile.getAbsolutePath());
		} catch (IOException e) {
			System.out.println("이미지 저장 실패: " + e.getMessage());
		}
	}

	public static void deleteAllFilesInDataFolder() {
		File folder = new File("./data");

		// 폴더가 없으면 생성
		if (!folder.exists()) {
			boolean created = folder.mkdirs(); // mkdir()도 가능하지만 mkdirs()는 상위 폴더까지 생성
			if (created) {
				System.out.println("data 폴더가 존재하지 않아 새로 생성했습니다.");
			} else {
				System.out.println("data 폴더를 생성하는 데 실패했습니다.");
				return;
			}
		}

		File[] files = folder.listFiles();

		if (files == null || files.length == 0) {
			System.out.println("data 폴더에 삭제할 파일이 없습니다.");
			return;
		}

		for (File file : files) {
			if (file.isFile()) {
				boolean deleted = file.delete();
				System.out.printf("파일 %s 삭제 %s%n", file.getName(), deleted ? "성공" : "실패");
			}
		}
	}
}