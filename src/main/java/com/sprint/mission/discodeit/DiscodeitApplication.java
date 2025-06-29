package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.*;
import com.sprint.mission.discodeit.entity.LoginType;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static UserResponseDto setupUser(UserService userService, UserCreateDto dto) {
        return userService.create(dto);
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        AuthService authService = context.getBean(AuthService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);
        ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
        UserStatusService userStatusService = context.getBean(UserStatusService.class);
        BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);

        // 유저 생성
        UserResponseDto user1 = setupUser(userService, new UserCreateDto(null, "user1", "google@google.com", "asdf1234", false, null));
        UserResponseDto user2 = setupUser(userService, new UserCreateDto(null, "user2", "googl1@google.com", "asdf1234", true, new byte[0]));
        System.out.println("user1 생성 : " + user1.getId());
        System.out.println("user2 생성 : " + user2.getId());

        // 중복 이메일 테스트
        try {
            setupUser(userService, new UserCreateDto(null, "user3", "google@google.com", "asdf1234", false, null));
        } catch (IllegalArgumentException e) {
            System.out.println("중복 이메일 체크 성공: " + e.getMessage());
        }

        // 유저 조회 테스트
        System.out.println("조회된 유저: " + userService.find(user1.getId()));

        // 유저 전체 조회
        List<UserResponseDto> users = userService.findAll();
        System.out.println("전체 유저 수: " + users.size());

        // 유저 수정 테스트
        UserResponseDto updatedUser = userService.update(new UserCreateDto(user1.getId(), "유저1 업데이트", "google@google.com", "asdf1234", true, new byte[0]));
        System.out.println("수정된 유저: " + updatedUser);

        // 유저 삭제
        userService.delete(user2.getId());
        System.out.println("user2 삭제 완료");

        // 삭제된 유저 조회 예외 테스트
        try {
            userService.find(user2.getId());
        } catch (NoSuchElementException e) {
            System.out.println("찾을 수 없는 유저입니다.");
        }

        // 로그인 테스트
        try {
            AuthRequestDto loginRequest = new AuthRequestDto("google@google.com", "asdf1234");
            UserResponseDto loginUser = authService.login(loginRequest);
            System.out.println("로그인 성공: " + loginUser);
        } catch (Exception e) {
            System.out.println("로그인 실패: " + e.getMessage());
        }

        // 잘못된 이메일 로그인
        try {
            authService.login(new AuthRequestDto("notfound@google.com", "asdf1234"));
            System.out.println("예외가 발생하지 않음 (실패해야 함)");
        } catch (IllegalArgumentException e) {
            System.out.println("로그인 실패 (정상): " + e.getMessage());
        }

        // 로그아웃
        try {
            authService.logout(user1.getId());
            System.out.println("로그아웃 성공");
        } catch (Exception e) {
            System.out.println("로그아웃 실패: " + e.getMessage());
        }

        // 존재하지 않는 유저 로그아웃
        try {
            authService.logout(UUID.randomUUID());
            System.out.println("존재하지 않는 유저 로그아웃 처리 완료");
        } catch (Exception e) {
            System.out.println("존재하지 않는 유저 로그아웃 실패: " + e.getMessage());
        }

        // 퍼블릭 채널 생성
        PublicChannelCreateDto publicDto = new PublicChannelCreateDto("일반 채널", "이건 테스트용 채널입니다.");
        ChannelResponseDto publicChannel = channelService.createPublicChannel(publicDto);
        System.out.println("채널명: " + publicChannel.getName());
        System.out.println("채널 타입: " + publicChannel.getType());

        // 동일한 이름으로 퍼블릭 채널 중복 생성 시도 테스트
        try {
            PublicChannelCreateDto duplicatePublicDto = new PublicChannelCreateDto("일반 채널", "중복 채널 생성 테스트");
            channelService.createPublicChannel(duplicatePublicDto);
            System.out.println("예외가 발생하지 않음 (실패해야 함)");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 채널 생성 차단 성공 (정상): " + e.getMessage());
        }

        // 프라이빗 채널 생성
        List<UUID> privateUsers = List.of(user1.getId());
        PrivateChannelCreateDto privateDto = new PrivateChannelCreateDto("비밀 채널", privateUsers);
        ChannelResponseDto privateChannel = channelService.createPrivateChannel(privateDto);
        System.out.println("채널명: " + privateChannel.getName());
        System.out.println("채널 타입: " + privateChannel.getType());

        // 채널 단건 조회
        try {
            ChannelResponseDto foundChannel = channelService.find(publicChannel.getChannelId());
            System.out.println("채널 조회 성공: " + foundChannel.getName());
        } catch (NoSuchElementException e) {
            System.out.println("채널 조회 실패: " + e.getMessage());
        }

        // 유저의 모든 채널 조회
        List<ChannelResponseDto> userChannels = channelService.findAllByUserId(user1.getId());
        System.out.println("user1이 가입된 채널 수: " + userChannels.size());

        // 퍼블릭 채널 수정
        try {
            ChannelUpdateRequestDto updateDto = new ChannelUpdateRequestDto(publicChannel.getChannelId(), "변경된 이름", "변경된 설명");
            ChannelResponseDto updatedChannel = channelService.update(updateDto);
            System.out.println("채널 수정 성공: " + updatedChannel.getName());
        } catch (Exception e) {
            System.out.println("채널 수정 실패: " + e.getMessage());
        }

        // 프라이빗 채널 수정 불가 테스트
        try {
            channelService.update(new ChannelUpdateRequestDto(privateChannel.getChannelId(), "수정 안됨", "불가"));
            System.out.println("예외가 발생하지 않음 (실패해야 함)");
        } catch (IllegalArgumentException e) {
            System.out.println("프라이빗 채널 수정 실패 (정상): " + e.getMessage());
        }

        // 퍼블릭 채널 삭제
        channelService.delete(publicChannel.getChannelId());
        System.out.println("퍼블릭 채널 삭제 완료");

        // 존재하지 않는 채널 삭제
        try {
            channelService.delete(UUID.randomUUID());
            System.out.println("예외가 발생하지 않음 (실패해야 함)");
        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 채널 삭제 실패 (정상): " + e.getMessage());
        }

        // 메시지 생성
        MessageCreateDto messageCreateDto = new MessageCreateDto("안녕하세요, 첫 번째 메시지입니다.", privateChannel.getChannelId(), user1.getId(), false, null);
        MessageResponseDto message1 = messageService.create(messageCreateDto);
        System.out.println("메시지 생성 성공: " + message1.getMessageId());

        // 메시지 조회
        try {
            MessageResponseDto foundMessage = messageService.find(message1.getMessageId());
            System.out.println("메시지 조회 성공: " + foundMessage.getContent());
        } catch (NoSuchElementException e) {
            System.out.println("메시지 조회 실패: " + e.getMessage());
        }

        // 채널 내 메시지 전체 조회
        List<MessageResponseDto> messagesInChannel = messageService.findallByChannelId(privateChannel.getChannelId());
        System.out.println("채널 내 메시지 수: " + messagesInChannel.size());

        // 메시지 수정
        try {
            MessageResponseDto updatedMessage = messageService.update(new MessageUpdateDto(message1.getMessageId(), "수정된 메시지 내용입니다.",true,List.of(new byte[0])));
            System.out.println("메시지 수정 성공: " + updatedMessage.getContent());
        } catch (NoSuchElementException e) {
            System.out.println("메시지 수정 실패: " + e.getMessage());
        }

        // 메시지 삭제
        try {
            messageService.delete(message1.getMessageId());
            System.out.println("메시지 삭제 성공");
        } catch (NoSuchElementException e) {
            System.out.println("메시지 삭제 실패: " + e.getMessage());
        }

        // 삭제된 메시지 조회 테스트
        try {
            messageService.find(message1.getMessageId());
            System.out.println("삭제된 메시지가 조회됨 (실패해야 함)");
        } catch (NoSuchElementException e) {
            System.out.println("삭제된 메시지 조회 실패 (정상): " + e.getMessage());
        }

        // 첨부파일 있는 메시지 생성
        List<byte[]> fileList = List.of(new byte[0], new byte[0]);

        MessageCreateDto fileMessageDto = new MessageCreateDto(
                "파일이 첨부된 메시지입니다.",
                privateChannel.getChannelId(),
                user1.getId(),
                true,
                fileList
        );

        MessageResponseDto fileMessage = null;
        try {
            fileMessage = messageService.create(fileMessageDto);
            System.out.println("첨부파일 포함 메시지 생성 성공: " + fileMessage.getMessageId());
            System.out.println("첨부된 바이너리 ID 수: " + fileMessage.getBinaryIds().size());
        } catch (Exception e) {
            System.out.println("첨부파일 메시지 생성 실패: " + e.getMessage());
        }

        // 첨부파일 메시지 삭제
        if (fileMessage != null) {
            try {
                messageService.delete(fileMessage.getMessageId());
                System.out.println("첨부파일 메시지 삭제 성공");
            } catch (NoSuchElementException e) {
                System.out.println("첨부파일 메시지 삭제 실패: " + e.getMessage());
            }
        }

        // 삭제된 첨부파일 메시지 조회 테스트
        if (fileMessage != null) {
            try {
                messageService.find(fileMessage.getMessageId());
                System.out.println("삭제된 첨부파일 메시지가 조회됨 (실패해야 함)");
            } catch (NoSuchElementException e) {
                System.out.println("삭제된 첨부파일 메시지 조회 실패 (정상): " + e.getMessage());
            }
        }

        // 존재하지 않는 메시지 삭제
        try {
            messageService.delete(UUID.randomUUID());
            System.out.println("예외가 발생하지 않음 (실패해야 함)");
        } catch (NoSuchElementException e) {
            System.out.println("존재하지 않는 메시지 삭제 실패 (정상): " + e.getMessage());
        }

        // 읽음 상태 테스트용 유저 새로 생성
        UserResponseDto readUser = setupUser(userService, new UserCreateDto(null, "readUser", "readuser@google.com", "asdf1234", false, null));
        System.out.println("읽음 상태 테스트용 유저 생성: " + readUser.getId());

        // 읽음 상태 생성
        ReadStatusResponseDto readStatus = null;
        try {
            ReadStatusCreateDto createDto = new ReadStatusCreateDto(readUser.getId(),privateChannel.getChannelId());
            readStatus = readStatusService.create(createDto);
            System.out.println("읽음 상태 생성 성공: " + readStatus.getId());
        } catch (Exception e) {
            System.out.println("읽음 상태 생성 실패: " + e.getMessage());
        }

        // 중복 생성 방지 테스트
        try {
            readStatusService.create(new ReadStatusCreateDto(readUser.getId(),privateChannel.getChannelId()));
            System.out.println("예외 발생 안됨 (실패해야 함)");
        } catch (IllegalArgumentException e) {
            System.out.println("중복 읽음 상태 생성 차단 성공 (정상): " + e.getMessage());
        }

        // 조회 테스트
        if (readStatus != null) {
            try {
                ReadStatusResponseDto found = readStatusService.findById(readStatus.getId());
                System.out.println("읽음 상태 조회 성공: " + found.getId());
            } catch (Exception e) {
                System.out.println("읽음 상태 조회 실패: " + e.getMessage());
            }
        }

        // 유저 기반 전체 읽음 상태 확인
        try {
            List<ReadStatusResponseDto> list = readStatusService.findAllByUserId(readUser.getId());
            System.out.println("readUser의 읽음 상태 수: " + list.size());
        } catch (Exception e) {
            System.out.println("유저의 읽음 상태 조회 실패: " + e.getMessage());
        }

        // 업데이트 테스트
        if (readStatus != null) {
            try {
                ReadStatusUpdateDto updateDto = new ReadStatusUpdateDto(readStatus.getId(), Instant.now());
                ReadStatusResponseDto updated = readStatusService.update(updateDto);
                System.out.println("읽음 시간 업데이트 성공: " + updated.getLastReadAt());
            } catch (Exception e) {
                System.out.println("읽음 상태 업데이트 실패: " + e.getMessage());
            }
        }

        // 삭제 테스트
        if (readStatus != null) {
            try {
                readStatusService.delete(readStatus.getId());
                System.out.println("읽음 상태 삭제 성공");
            } catch (Exception e) {
                System.out.println("읽음 상태 삭제 실패: " + e.getMessage());
            }
        }

        // 삭제된 읽음 상태 조회
        if (readStatus != null) {
            try {
                readStatusService.findById(readStatus.getId());
                System.out.println("삭제된 읽음 상태가 조회됨 (실패해야 함)");
            } catch (IllegalArgumentException e) {
                System.out.println("삭제된 읽음 상태 조회 실패 (정상): " + e.getMessage());
            }
        }

        // 유저 상태 테스트
        try {
            List<UserStatusResponseDto> statusList = userStatusService.findByAll();
            System.out.println("전체 유저 상태 수: " + statusList.size());
        } catch (Exception e) {
            System.out.println("유저 상태 전체 조회 실패: " + e.getMessage());
        }

        // 특정 유저 상태 조회
        UserStatusResponseDto userStatus = null;
        try {
            UUID userId = user1.getId(); // 이미 생성된 유저
            userStatus = userStatusService.updateByUserId(
                    new UserStatusUpdateDto(null, userId, LoginType.ONLINE));
            System.out.println("초기 조회용 상태 객체 업데이트 후 ID: " + userStatus.getId());
        } catch (Exception e) {
            System.out.println("상태 초기 업데이트 실패: " + e.getMessage());
        }

        // 상태 ID 기반 단건 조회
        if (userStatus != null) {
            try {
                UserStatusResponseDto found = userStatusService.findById(userStatus.getId());
                System.out.println("유저 상태 조회 성공: " + found.getId() + " (" + found.getLoginType() + ")");
            } catch (Exception e) {
                System.out.println("유저 상태 조회 실패: " + e.getMessage());
            }
        }

        // 상태 ID 기반 수정 테스트
        if (userStatus != null) {
            try {
                UserStatusUpdateDto updateDto = new UserStatusUpdateDto(
                        userStatus.getId(), null, LoginType.OFFLINE);
                UserStatusResponseDto updated = userStatusService.update(updateDto);
                System.out.println("상태 직접 수정 성공: " + updated.getLoginType());
            } catch (Exception e) {
                System.out.println("유저 상태 직접 수정 실패: " + e.getMessage());
            }
        }

        // 이진 데이터 생성 테스트
        BinaryContentResponseDto binary1 = null;
        BinaryContentResponseDto binary2 = null;
        try {
            BinaryContentCreateDto dto1 = new BinaryContentCreateDto("FILE", new byte[0]);
            BinaryContentCreateDto dto2 = new BinaryContentCreateDto("IMAGE", new byte[0]);

            binary1 = binaryContentService.create(dto1);
            binary2 = binaryContentService.create(dto2);

            System.out.println("이진 콘텐츠 생성 성공: " + binary1.getId() + ", " + binary2.getId());
        } catch (Exception e) {
            System.out.println("이진 콘텐츠 생성 실패: " + e.getMessage());
        }

        // 단건 조회 테스트
        if (binary1 != null) {
            try {
                BinaryContentResponseDto found = binaryContentService.find(binary1.getId());
                System.out.println("이진 콘텐츠 조회 성공: " + found.toString());
            } catch (Exception e) {
                System.out.println("이진 콘텐츠 조회 실패: " + e.getMessage());
            }
        }

        // 여러 개 조회 테스트
        try {
            List<UUID> ids = List.of(binary1.getId(), binary2.getId());
            List<BinaryContentResponseDto> list = binaryContentService.findAllByIdIn(ids);
            System.out.println("이진 콘텐츠 여러 개 조회 성공: " + list.size() + "개");
        } catch (Exception e) {
            System.out.println("이진 콘텐츠 여러 개 조회 실패: " + e.getMessage());
        }

        // 삭제 테스트
        if (binary1 != null) {
            try {
                binaryContentService.delete(binary1.getId());
                System.out.println("이진 콘텐츠 삭제 성공: " + binary1.getId());
            } catch (Exception e) {
                System.out.println("이진 콘텐츠 삭제 실패: " + e.getMessage());
            }
        }

        // 삭제 후 조회 테스트
        if (binary1 != null) {
            try {
                binaryContentService.find(binary1.getId());
                System.out.println("삭제된 이진 콘텐츠가 조회됨 (실패해야 함)");
            } catch (IllegalArgumentException e) {
                System.out.println("삭제된 이진 콘텐츠 조회 실패 (정상): " + e.getMessage());
            }
        }

        // 존재하지 않는 ID로 삭제 시도
        try {
            binaryContentService.delete(UUID.randomUUID());
            System.out.println("예외 발생 안됨 (실패해야 함)");
        } catch (IllegalArgumentException e) {
            System.out.println("존재하지 않는 이진 콘텐츠 삭제 실패 (정상): " + e.getMessage());
        }
    }
}