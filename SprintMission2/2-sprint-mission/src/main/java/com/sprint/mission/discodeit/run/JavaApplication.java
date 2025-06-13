package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import java.io.File;

public class JavaApplication {
  // 테스트를 반복하면 데이터가 중복되는 현상때문에 파일을 다 지움
  public static void clearDataFiles() {
    String[] dataFiles = {"data/user.ser", "data/channel.ser", "data/message.ser"};

    for (String path : dataFiles) {
      File file = new File(path);
      if (file.exists()) {
        if (file.delete()) {
          System.out.println("삭제됨: " + path);
        } else {
          System.out.println("삭제 실패: " + path);
        }
      }
    }
  }

  static User setupUser(UserService userService) {
    return userService.addUser("Alice");
  }

  static Channel setupChannel(ChannelService channelService, User user) {
    return channelService.createChannel("Acrobatic", user);
  }

  static Message setupMessage(MessageService messageService, User user, Channel channel) {
    Message message = messageService.addMessage("Hello World", user, channel);
    System.out.println("메세지 생성: " + message.getMessageId());
    return message;
  }

  public static void main(String[] args) {
    // 실행 전 데이터 초기화
    clearDataFiles();

    // 파일 서비스 초기화
    UserService fileUserService = ServiceFactory.createFileUserService();
    ChannelService fileChannelService = ServiceFactory.createFileChannelService();
    MessageService fileMessageService = ServiceFactory.createFileMessageService();

    User user = setupUser(fileUserService);
    Channel channel = setupChannel(fileChannelService, user);
    Message message = setupMessage(fileMessageService, user, channel);

    fileUserService.updateUser(user.getUserId(), "Bob");
    fileMessageService.updateMessage(message.getMessageId(), "Hi");
    fileChannelService.updateChannelName(channel.getChannelId(), "Baseball");

    fileUserService.findAllUser().forEach(System.out::println);
    fileChannelService.findAllChannel().forEach(System.out::println);
    fileMessageService.findAllMessage().forEach(System.out::println);

    //        jcf 서비스 초기화
    //        UserService jcfUserService = ServiceFactory.createJCFUserService();
    //        ChannelService jcfChannelService = ServiceFactory.createJCFChannelService();
    //        MessageService jcfMessageService = ServiceFactory.createJCFMessageService();
  }
}
