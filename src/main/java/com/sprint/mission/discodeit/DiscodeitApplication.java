package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserStatusDto.UserStatusUpdateDto;
import com.sprint.mission.discodeit.dto.auth.AuthLoginDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelPrivateCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelPublicCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusCreateDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponseDto;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusUpdateDto;
import com.sprint.mission.discodeit.dto.user.UserCreateDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DiscodeitApplication {

    static UserResponseDto setupUser(UserService userService) {
        UserResponseDto userResponseDto = userService.create(new UserCreateDto("유저", "user@gmail.com", "user1234",
                new BinaryContentCreateDto(null, null, BinaryContentType.PROFILE, new byte[]{1, 2, 3, 4}, FileType.PNG, "profile", 250L)));
        return userResponseDto;
    }

    static ChannelResponseDto setupChannel(ChannelService channelService) {
        ChannelResponseDto channelResponseDto = channelService.create(new ChannelPublicCreateDto(ChannelType.PUBLIC, "채널", "PUBLIC 채널입니다."));
        return channelResponseDto;
    }

    static ChannelResponseDto setUpPrivateChannel(ChannelService channelService, List<UUID> userIds) {
        List<ReadStatusCreateDto> readStatusCreateDtos = new ArrayList<>();
        for (UUID userId : userIds) {
            readStatusCreateDtos.add(new ReadStatusCreateDto(userId, null));
        }
        ChannelResponseDto privateChannelResponseDto = channelService.create(new ChannelPrivateCreateDto(ChannelType.PRIVATE, userIds, readStatusCreateDtos));
        return privateChannelResponseDto;
    }

    static MessageResponseDto messageCreateTest(MessageService messageService, UUID authorId, UUID channelId) {
        List<BinaryContentCreateDto> binaryContentCreateDtos = new ArrayList<>();
        BinaryContentCreateDto binaryContentCreateDto = new BinaryContentCreateDto(null, null, BinaryContentType.ATTACHMENT, new byte[]{1,2,3,4}, FileType.GIF, "메시지 첨부파일", 50L);
        binaryContentCreateDtos.add(binaryContentCreateDto);
        MessageResponseDto messageResponseDto = messageService.create(new MessageCreateDto(authorId, channelId, "메시지", binaryContentCreateDtos));
        System.out.println("메시지 생성: " + messageResponseDto.id());
        return messageResponseDto;
    }

    static void updateUser(UserService userService, UUID userId, String newUsername, String newUserEmail, String newUserPassword, BinaryContentCreateDto newBinaryContentDto) {
        userService.update(new UserUpdateDto(
                userId,
                newUsername,
                newUserEmail,
                newUserPassword,
                newBinaryContentDto
        ));
    }

    static void updateUserStatusByUserId(UserStatusService userStatusService, UUID userId, Instant newLastLoginTime) {
        UserStatusUpdateDto userStatusUpdateDto = new UserStatusUpdateDto(null, newLastLoginTime);
        userStatusService.updateByUserId(userStatusUpdateDto, userId);
    }

    static ReadStatusResponseDto readStatusCreateTest(ReadStatusService readStatusService, UUID userId, UUID chanelId) {
        ReadStatusCreateDto readStatusCreateDto = new ReadStatusCreateDto(userId, chanelId);
        return readStatusService.create(readStatusCreateDto);
    }

    static void updateReadStatus(ReadStatusService readStatusService, UUID readStatusId, Instant newReadTime) {
        ReadStatusUpdateDto readStatusUpdateDto = new ReadStatusUpdateDto(readStatusId, newReadTime);
        readStatusService.update(readStatusUpdateDto);
    }

    static BinaryContentResponseDto binaryContentCreateTest(BinaryContentService binaryContentService, UUID userId, UUID messageId, BinaryContentType binaryContentType, byte[] bytes, FileType fileType, String fileName, Long fileSize) {
        BinaryContentCreateDto binaryContentCreateDto = new BinaryContentCreateDto(
                userId,
                messageId,
                binaryContentType,
                bytes,
                fileType,
                fileName,
                fileSize
        );
        return binaryContentService.create(binaryContentCreateDto);
    }

    public static void main(String[] args) {
        // 스프링 시작
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        AuthService authService = context.getBean(AuthService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

        // 셋업
        UserResponseDto user = setupUser(userService);
        ChannelResponseDto channel = setupChannel(channelService);

        // 테스트
        MessageResponseDto message = messageCreateTest(messageService, user.id(), channel.id());
        System.out.println(userService.find(user.id()));

        System.out.println(userService.findAll());
        updateUser(userService, user.id(),"홍길동", "gildong@gmail.com", "gildong1234", null);
        System.out.println(userService.find(user.id()));

//        userService.delete(user.getId());
//        System.out.println(userService.findAll());

        authService.login(new AuthLoginDto("홍길동", "gildong1234"));
//        authService.login(new AuthLoginDto("홍길동", "gildong1235"));

        messageService.delete(message.id());

        // PRIVATE Channel
        List<UUID> participantUserIds = new ArrayList<>();
        participantUserIds.add(user.id());
        ChannelResponseDto privateChannel = setUpPrivateChannel(channelService, participantUserIds);
        System.out.println(channelService.find(privateChannel.id()));

        // ReadStatus
        ReadStatusResponseDto readStatus = readStatusCreateTest(readStatusService, user.id(), channel.id());
        System.out.println(readStatusService.find(readStatus.id()));

        String timeString = "2004-04-28T01:41:02Z";
        Instant newReadTime = Instant.parse(timeString);
        updateReadStatus(readStatusService, readStatus.id(), newReadTime);
        System.out.println(readStatusService.find(readStatus.id()));

        readStatusService.delete(readStatus.id());
        System.out.println(readStatusService.findAllByUserId(user.id()));

        // UserStatus
        System.out.println(userStatusService.findByUserId(user.id()));
        updateUserStatusByUserId(userStatusService, user.id(), Instant.parse(timeString));
        System.out.println(userStatusService.findByUserId(user.id()));

        // BinaryContent
        BinaryContentResponseDto binaryContent = binaryContentCreateTest(binaryContentService, user.id(), message.id(), BinaryContentType.ATTACHMENT, new byte[]{10, 20, 30}, FileType.GIF, "고양이 날아오르는 움짤", 120L);
        System.out.println(binaryContentService.find(binaryContent.id()));

        List<UUID> binaryContentIds = new ArrayList<>();
        binaryContentIds.add(binaryContent.id());
        binaryContentIds.add(user.profileId());
        System.out.println(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}

