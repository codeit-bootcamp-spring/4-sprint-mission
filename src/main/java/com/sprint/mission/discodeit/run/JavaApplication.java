package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.factory.JCFServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;

public class JavaApplication {

    public static void main(String[] args) {
        JCFServiceFactory serviceFactory = new JCFServiceFactory();
        UserService userService = serviceFactory.createUserService();
        ChannelService channelService = serviceFactory.createChannelService();
        MessageService messageService = serviceFactory.createMessageService();

        // ===== UserService 테스트 =====
        // 1. 사용자 생성
        User spring = new User("spring@example.com", "password123", "spring", "Love Spring");
        User jpa = new User("jpa@example.com", "word124567", "jpa", "Good Jpa");
        User java = new User("java@example.com", "secure123", "java", "Fan Java");
        userService.createUser(spring);
        userService.createUser(jpa);
        userService.createUser(java);

        // 2, 단건 조회
        System.out.println("=== User 단건 조회 (Spring) ===");
        Optional<User> maybeSpring = userService.getUserByUserId(spring.getId());
        if (maybeSpring.isPresent()) {
            System.out.println(maybeSpring);
        } else {
            System.out.println("등록되지 않은 사용자 입니다.");
        }

        // 3. 다건 조회
        System.out.println("\n=== User 전체 조회 ===");
        List<User> allUsers = userService.getUsers();
        if (allUsers != null) {
            allUsers.forEach(System.out::println);
        } else {
            System.out.println("등록된 사용자가 없습니다.");
        }

        // 4. 사용자 업데이트 (displayName 변경)
        jpa.updateUserDisplayName("Nice Jpa");
        System.out.println("\n=== User 수정 (Good Jpa -> Nice Jpa) ===");
        if (allUsers != null) {
            userService.updateUser(jpa.getId(), jpa);
            System.out.println(jpa);
            System.out.println("\n=== User 수정 후 전체 조회===");
            allUsers.forEach(System.out::println);
        } else {
            System.out.println("업데이트 대상 사용자가 없습니다.");
        }

        // 5. 사용자 상태 업데이트 (UserStatus 변경)
        java.updateUserStatus(UserStatus.DELETED);
        System.out.println("\n=== UserStatus 수정 (ACTIVE -> DELETED) ===");
        Optional<User> javaStatus = userService.updateUser(java.getId(), java);
        if (allUsers != null) {
            System.out.println("변경된 상태: " + javaStatus.get().getStatus());
            allUsers.forEach(System.out::println);
        } else {
            System.out.println("업데이트 대상 사용자가 없습니다.");
        }

        // 6. 사용자 삭제 (by status)
        System.out.println("\n=== User 삭제 (Java by status) ===");
        if (allUsers != null) {
            userService.deleteUser(java, java.getStatus());
            System.out.println("=== User 삭제 후 전체 조회===");
            allUsers = userService.getUsers();
            if (allUsers != null) {
                allUsers.forEach(System.out::println);
            } else {
                System.out.println("등록된 사용자가 없습니다.");
            }
        } else {
            System.out.println("등록되지 않은 사용자입니다.");
        }

        // ===== ChannelService 테스트 =====

        // 1. 채널 생성
        Channel studyChannel = new Channel("I love Study");
        Channel gameChannel = new Channel("Game with me");
        channelService.createChannel(studyChannel);
        channelService.createChannel(gameChannel);

        // 2. 단건 조회
        System.out.println("\n=== Channel 단건 조회 (Study) ===");
        Optional<Channel> maybeStudyChannel = channelService.getChannelById(studyChannel.getId());
        if (maybeStudyChannel.isPresent()) {
            System.out.println(maybeStudyChannel);
        } else {
            System.out.println("해당 채널이 없습니다.");
        }

        // 3. 다건 조회
        System.out.println("\n=== Channel 전체 조회 ===");
        List<Channel> allChannels = channelService.getChannels();
        if (allChannels != null) {
            allChannels.forEach(System.out::println);
        } else {
            System.out.println("등록된 채널이 없습니다.");
        }

        // 4. 채널명 업데이트 (channelName 변경)
        System.out.println("\n=== Channel 수정 (I love Study -> I hate Study) ===");
        if (allChannels != null) {
            channelService.updateChannel(studyChannel.getId(), "I hate Study");
            System.out.println(studyChannel);
            System.out.println("\n=== User 수정 후 전체 조회===");
            allChannels.forEach(System.out::println);
        } else {
            System.out.println("업데이트 대상 채널이 없습니다.");
        }

        // 5. 사용자 채널 참여
        System.out.println("\n=== User JPA를 Channel Game with me에 참여시키기 ===");
        if (allChannels != null && allUsers != null) {
            channelService.joinUserToChannel(gameChannel, jpa);
            System.out.println("채널 '" + gameChannel.getChannelName() + "'의 사용자 목록:");
            List<User> usersInGame = gameChannel.getUsers();

            if (usersInGame != null) {
                usersInGame.forEach(System.out::println);
            } else {
                System.out.println("등록된 사용자가 없습니다.");
            }
        } else {
            System.out.println("채널 또는 사용자 정보가 없습니다.");
        }

        // 6. 사용자 채널 퇴장
        System.out.println("\n=== User JPA를 Channel Game with me에서 퇴장시키기===");
        if (allChannels != null && allUsers != null) {
            channelService.deleteUserToChannel(gameChannel, jpa);
            System.out.println("채널 '" + gameChannel.getChannelName() + "'의 사용자 목록:");
            List<User> usersInGame = gameChannel.getUsers();

            if (!usersInGame.isEmpty()) {
                usersInGame.forEach(System.out::println);
            } else {
                System.out.println("등록된 사용자가 없습니다.");
            }
        } else {
            System.out.println("채널 또는 사용자 정보가 없습니다.");
        }


        // 7. 채널 삭제 (by channelId)
        System.out.println("\n=== Channel 삭제 (I hate Study by channel) ===");
        if (studyChannel.getId() != null) {
            channelService.deleteChannel(studyChannel);
            System.out.println("=== Channel 삭제 후 전체 조회===");
            allChannels = channelService.getChannels();
            allChannels.forEach(System.out::println);
        } else {
            System.out.println("삭제 대상 채널이 없습니다.");
        }

        // ===== MessageService 테스트 =====

        // 1. 메세지 전송
        System.out.println("\n=== Message 전송 ===");
        Message m1 = messageService.sendMessage(gameChannel, jpa, jpa.getDisplayName(), "Hello everyone");
        Message m2 = messageService.sendMessage(gameChannel, jpa, jpa.getDisplayName(), "Welcome to Game with me Channel");

        // 2. 다건 조회
        System.out.println("전송된 메시지:");
        List<Message> sentMessages = messageService.getMessages();
        if (sentMessages != null) {
            sentMessages.forEach(System.out::println);
        } else {
            System.out.println("전송된 메세지가 없습니다.");
        }

        // 3. 단건 조회
        System.out.println("\n=== Message 단건 조회 (첫 번째 메시지) ===");
        Optional<Message> maybeM1Message = messageService.getMessageById(m1.getId());
        if (maybeM1Message.isPresent()) {
            System.out.println(maybeM1Message);
        } else {
            System.out.println("메세지를 찾을 수 없습니다.");
        }

        // 4. 채널별 메시지 조회
        System.out.println("\n=== Channel별 메시지 조회 (gameChannel) ===");
        if (allChannels != null) {

            List<Message> msgsInGameChannel = messageService.getMessageByChannel(gameChannel);
            if (msgsInGameChannel != null) {
                msgsInGameChannel.forEach(System.out::println);
            } else {
                System.out.println("해당 채널에 전송된 메세지가 없습니다.");
            }
        } else {
            System.out.println("채널 정보가 없습니다.");
        }
    }
}



