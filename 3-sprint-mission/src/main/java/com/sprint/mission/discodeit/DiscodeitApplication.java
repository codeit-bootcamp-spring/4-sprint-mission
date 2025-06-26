package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.DiscodeitProperties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.login.LoginRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(DiscodeitProperties.class)
public class DiscodeitApplication {

    // 테스트를 반복하면 데이터가 중복되는 현상때문에 파일을 다 지움
    public static void clearDataFiles() {
        File dataDir = new File("data");

        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        System.out.println("삭제됨: " + file.getName());
                    } else {
                        System.out.println("삭제 실패: " + file.getName());
                    }
                }
            }
            if (!dataDir.delete()) {
                System.out.println("data 디렉터리 삭제 실패");
            } else {
                System.out.println("data 디렉터리 삭제됨");
            }
        }

        if (!dataDir.exists()) {
            if (dataDir.mkdir()) {
                System.out.println("data 디렉터리 생성됨");
            } else {
                System.out.println("data 디렉터리 생성 실패");
            }
        }
    }

    public static void main(String[] args) {
        clearDataFiles();
        ConfigurableApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        // Service 주입
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);

        // 유저 생성
        UserResponseDto user1 =
                userService.createUser(new UserCreateDto("Alice", "alice@google.com", "1234", null, true));
        UserResponseDto user2 =
                userService.createUser(new UserCreateDto("Bob", "bob@google.com", "5678", null, true));
        UserResponseDto user3 =
                userService.createUser(
                        new UserCreateDto("Charlie", "charlie@google.com", "9012", null, true));

        // 유저 로그인 → 접속 시간 갱신
        authService.login(new LoginRequestDto("Alice", "1234"));

        UserStatusResponseDto userStatusResponseDto =
                userStatusService.updateUserStatusByUserId(user1.id());

        // 채널 생성 및 이름 변경
        ChannelResponseDto channel1 =
                channelService.createPublicChannel(
                        new ChannelCreateDto("General", "채팅방", ChannelType.PUBLIC, user1.id()));
        List<UserResponseDto> users = new ArrayList<>();
        users.add(user2);
        ChannelResponseDto channel2 = channelService.createPrivateChannel(users);
        ChannelResponseDto channel3 =
                channelService.createPublicChannel(
                        new ChannelCreateDto("Example", "삭제 될 채팅방", ChannelType.PUBLIC, user3.id()));
        channelService.updateChannelName(new ChannelUpdateDto(channel1.id(), "DailyTalk"));

        // 채널 삭제
        channelService.deleteChannel(channel3.id());

        // 채널 참가
        channelService.joinUser(channel1.id(), user3.id());
        channelService.joinUser(channel2.id(), user3.id());
        channelService.leaveUser(channel2.id(), user3.id());

        // 첨부파일 생성
        BinaryContentResponseDto binaryContent1 =
                binaryContentService.createBinaryContent(
                        new BinaryContentCreateDto(user1.id(), null, null, null));
        binaryContentService.deleteBinaryContent(binaryContent1.id());
        BinaryContentResponseDto binaryContent =
                binaryContentService.createBinaryContent(
                        new BinaryContentCreateDto(user1.id(), null, null, null));

        // 메시지 생성
        MessageResponseDto message =
                messageService.createMessage(
                        new MessageCreateDto(
                                "처음 메시지입니다",
                                List.of(new BinaryContentRequestDto(null, null)),
                                user1.id(),
                                channel1.id()));

        // 메시지 수정
        messageService.updateMessage(
                new MessageUpdateDto(
                        message.id(),
                        "수정된 메시지",
                        List.of(), // 삭제할 첨부 없음
                        List.of(new BinaryContentRequestDto(null, null))));

        // 읽음 상태 생성 및 갱신
        ReadStatusResponseDto readStatus1 =
                readStatusService.createReadStatus(new ReadStatusCreateDto(user1.id(), channel1.id()));
        readStatusService.deleteReadStatus(readStatus1.id());

        ReadStatusResponseDto readStatus =
                readStatusService.createReadStatus(new ReadStatusCreateDto(user1.id(), channel1.id()));
        readStatusService.updateReadStatus(new ReadStatusUpdateDto(readStatus.id(), true));

        // 유저 삭제
        userService.deleteUser(user2.id());

        // 출력 확인
        System.out.println("\n==== 전체 사용자 ====");
        userService.findAllUser().forEach(System.out::println);

        System.out.println("\n==== 사용자 단건 조회 ====");
        System.out.println(userService.findUserById(user1.id()));

        System.out.println("\n==== 유저 이름 수정 후 ====");
        UserResponseDto updatedUser =
                userService.updateUser(new UserUpdateDto(user1.id(), "AliceUpdated", null));
        System.out.println(updatedUser);

        System.out.println("\n==== 유저가 쓴 모든 메시지 ====");
        userService.findMessagesByUser(user1.id()).forEach(System.out::println);

        System.out.println("\n==== 이름에 'Ali'가 포함된 유저 목록 ====");
        userService.findUsersByNameContains("Ali").forEach(System.out::println);

        System.out.println("\n==== 전체 채널 ====");
        channelService.findAllByUserId(user1.id()).forEach(System.out::println);

        System.out.println("\n==== 채널 단건 조회 ====");
        System.out.println(channelService.findChannelById(channel1.id()));

        System.out.println("\n==== 채널 이름 검색 ====");
        channelService.findChannelsByNameContains("Daily").forEach(System.out::println);

        System.out.println("\n==== 채널에서 유저를 찾아 메세지를 출력 ====");
        channelService
                .findMessagesByUserInChannel(channel1.id(), user1.id())
                .forEach(System.out::println);

        System.out.println("\n==== 채널 메시지 ====");
        messageService.findAllByChannelId(channel1.id()).forEach(System.out::println);

        System.out.println("\n==== 메시지 내용으로 검색 ====");
        messageService.findMessagesByContentContains("수정").forEach(System.out::println);

        System.out.println("\n==== 특정 메시지 단건 조회 ====");
        System.out.println(messageService.findMessageById(message.id()));

        System.out.println("\n==== 특정 ID로 바이너리 콘텐츠 조회 ====");
        List<UUID> allBinaryIds = new ArrayList<>();
        allBinaryIds.add(binaryContent.id());
        binaryContentService.findAllBinaryContentByIdIn(allBinaryIds).forEach(System.out::println);

        System.out.println("\n==== 바이너리 콘텐츠 ====");
        System.out.println(binaryContentService.findBinaryContentById(binaryContent.id()));

        System.out.println("\n==== 읽음 상태 ====");
        readStatusService.findAllReadStatus(user1.id()).forEach(System.out::println);

        System.out.println("\n==== 읽음 상태 단건 조회 ====");
        System.out.println(readStatusService.findReadStatus(readStatus.id()));

        System.out.println("\n==== 전체 사용자 상태 ====");
        userStatusService.findAllUserStatus().forEach(System.out::println);

        System.out.println("\n==== 사용자 상태 ====");
        System.out.println(userStatusService.findUserStatusById(userStatusResponseDto.id()));

        messageService.deleteMessage(message.id());

        System.out.println("\n==== 사용자 상태 삭제 후 전체 목록 ====");
        userStatusService.deleteUserStatusById(userStatusResponseDto.id());
        userStatusService.findAllUserStatus().forEach(System.out::println);
    }
}
