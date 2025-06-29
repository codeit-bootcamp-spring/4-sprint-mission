package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.DTO.*;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {

    static UserResponse setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest("woody", "woody@codeit.com", "woody1234", null);
        return userService.create(request);
    }

    static ChannelCreateResponse setupChannel(ChannelService channelService) {
        PublicChannelRequest request = new PublicChannelRequest(ChannelType.PRIVATE, "공지 채널입니다.","설명");
        return channelService.createPublicChannel(request);
    }

    static void messageCreateTest(MessageService messageService, UUID channelId, UUID authorId) {
        MessageCreateRequest request = new MessageCreateRequest("안녕하세요.", channelId, authorId, null);
        MessageResponse response = messageService.create(request);
        System.out.println("메시지 생성됨: " + response.id());
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        UserResponse user = setupUser(userService);

        ChannelCreateResponse channel = setupChannel(channelService);

        messageCreateTest(messageService, channel.id(), user.id());

        System.out.println("사용자 조회: " + userService.find(user.id()));

        System.out.println("전체 사용자 목록:");
        userService.findAll().forEach(System.out::println);

        UserUpdateRequest updateRequest = new UserUpdateRequest(user.id(), "buzz", "buzz@codeit.com", "buzz1234", null);
        UserResponse updatedUser = userService.update(updateRequest);
        System.out.println("수정된 사용자: " + updatedUser);

        System.out.println("채널 조회: " + channelService.find(channel.id()));

        System.out.println("전체 채널 목록:");
        channelService.findAll().forEach(System.out::println);

        System.out.println("채널 내 메시지 목록:");
        messageService.findAllByChannelId(channel.id()).forEach(System.out::println);

        userService.delete(user.id());
        System.out.println("사용자 삭제 완료");

        channelService.delete(channel.id());
        System.out.println("채널 삭제 완료");

    }

}
