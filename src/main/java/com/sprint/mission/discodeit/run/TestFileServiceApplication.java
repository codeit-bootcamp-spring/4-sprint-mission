package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;

public class TestFileServiceApplication implements Serializable {
    public static void main(String[] args) {
        testFileService();
        // sprint1 떄 만든 코드로써 저장 로직이 변경 되었으므로 data 폴던안의 파일들을 전부 삭제하면 돌아간다. -- sprint2 개발 중
    }

    public static void testFileService() {

        /*
        System.out.println("**************************************************************  *");
        System.out.println("\n========== userService Test start ==========================================\n");
        userTestApplication();
        System.out.println("\n========== userService Test end ==========================================\n");

        System.out.println("\n========== load userService Test start ==========================================\n");
        loadUserTestApplication();
        System.out.println("\n========== load userService Test end ==========================================\n");
        System.out.println("***************************************************************");*/

        // userService 테스트
        /*
        System.out.println("***************************************************************");
        System.out.println("\n========== channelService Test start ==========================================\n");
        //channelTestApplication();
        System.out.println("\n========== channelService Test end ==========================================\n");
        // channelService 테스트

        System.out.println("\n========== load channelService Test start ==========================================\n");
        //loadChannelTestApplication();
        System.out.println("\n========== load channelService Test end ==========================================\n");
        System.out.println("***************************************************************");
        // channelService 테스트
        */

        System.out.println("***************************************************************");
        System.out.println("\n========== messageService Test start ==========================================\n");
        messageTestApplication();
        System.out.println("\n========== messageService Test end ==========================================\n");
        // messageService 테스트

        System.out.println("***************************************************************");
        System.out.println("\n========== load messageService Test start ==========================================\n");
        loadMessageTestApplication();
        System.out.println("\n========== load messageService Test end ==========================================\n");
        System.out.println("***************************************************************");
        // messageService 테스트

        // 데이터를 계속 추가히나까 저장되는게 점점 더 많아진다.
        // 현재 출력되는게 정상
        // 좀 더 정상적으로 볼려면 testApplication을 하나씩 만 사용해야된다.
        // data 폴더 내의 파일들을 모두 지워야 보기편하다.
    }

    public static void userTestApplication() {

        UserService FileUserService = ServiceFactory.getInstance().getFileUserService();
        // sprint2 이므로 file 서비스를 이용하게 될것

        System.out.println("1. 유저 등록=================");
        User user1 = FileUserService.createUser("권용진-1");
        User user2 = FileUserService.createUser("권용진-2");
        System.out.println(); // 가독성을 위한 줄바꾸
        // 두 명의 유저를 등록

        System.out.println("2. 단일 유저 조회=================");
        FileUserService.printUser(user1);
        System.out.println();
        // 유저 한명의 정보를 단일 조회

        System.out.println("3. 전체 유저 조회=================");
        FileUserService.printActiveUsers();
        System.out.println();
        // 유저 전체의 정보를 조회
        System.out.println("4. 유저 정보 변경=================");
        FileUserService.updateUser(user1, "수정된 권용진-1");
        System.out.println();
        // 유저의 이름을 변경
        FileUserService.printUser(user1);
        System.out.println();
        // 변경된 유저의 정보를 조회
        System.out.println("5. 유저 삭제(비활성화)=================");
        FileUserService.deleteUser(user1);
        System.out.println();
        // 유저 한 명을 삭제를 통해 비활성화
        FileUserService.printActiveUsers(); // 조회
        System.out.println();
        // 정상적을 삭제 되었는지를 확인 하기 위한 활성화 상태인 유저 전체 조회
        FileUserService.printAllUsers(); // 조회
        System.out.println();
        // 활성화된 유저와 비활성화된 유저 전체 조회
        FileUserService.printDeactivatedUsers(); // 조회
        System.out.println();
        // 비활성화된 유저만 조회
        System.out.println("6. 유저 활성화===============");
        FileUserService.restoreUser(user1);
        System.out.println();
        FileUserService.printActiveUsers(); // 조회
        System.out.println();
        FileUserService.printDeactivatedUsers(); // 조회
        System.out.println();
    }

    public static void loadUserTestApplication() {
        System.out.println("1. 유저 파일 불러온 후 전체 유저 조회");
        UserService fileUserService = ServiceFactory.getInstance().getFileUserService();
        fileUserService.printAllUsers();
        System.out.println();
    }

    public static void channelTestApplication() {
        UserService fileUserService = ServiceFactory.getInstance().getFileUserService();
        ChannelService fileChannelService = ServiceFactory.getInstance().getFileChannelService();

        System.out.println("1. 유저 생성=================");
        User user1 = fileUserService.createUser("권용진-1");
        User user2 = fileUserService.createUser("권용진-2");
        System.out.println();
        // 채널을 만들기 위한 유저 생성

        System.out.println("2. 채널 생성=================");
        Channel channel1 = fileChannelService.createChannel(user1, "권용진-1-서버");
        Channel channel2 = fileChannelService.createChannel(user2, "권용진-2-서버");
        System.out.println();
        // 만들어진 유저를 이용하여 채널 생성
        System.out.println();
        System.out.println("3. 채널 전체 조회=================");
        fileChannelService.printAllChannels();
        System.out.println();
        // 생성된 채널 전체 조회
        System.out.println("4. 채널 단일 조회=================");
        fileChannelService.printChannel(channel1);
        System.out.println();
        // 생성된 채널 단일 조회

        System.out.println("5. 채널 정보 수정, 유저 채널 입장, 동일 유저가 중복 입장하려 할 때=================");
        fileChannelService.addUserToChannel(user2, channel1); // user2 번이 1번 채널에 추가됨
        // 이미 만들어진 채널에 다른 유저가 입장
        fileChannelService.addUserToChannel(user2, channel1); // user2 번이 1번 채널에 추가됨
        System.out.println();
        fileChannelService.printUsersFromChannel(channel1);
        System.out.println();

        // 유저가 새로이 입장한 채널의 정보를 조회하여 성공적으로 유저가 입장했는지 확인

        System.out.println("6. 채널 정보 수정, 채널 이름 수정, 권한 없는 유저가 수정하려 할 떄=================");
        fileChannelService.updateChannelName(user2, channel1, "권용진-1-서버의 새이름");
        // 채널 주인인 유저만 채널의 이름을 변경할 수 있음
        fileChannelService.printChannel(channel1);
        System.out.println();
        // 채널의 정보가 안변했음을 확인하기 위해 채널 정보 조회

        fileChannelService.updateChannelName(user1, channel1, "권용진-1-서버의 새이름");// 채널 이름 변경
        System.out.println();
        // 채널 주인은 채널의 이름을 변경할 수 있음

        fileChannelService.printAllChannels();
        System.out.println();
        // 변경된 채널 이름 조회

        System.out.println("7. 채널 정보 수정, 채널 내 유저 퇴장=================");
        fileChannelService.leaveUserFromChannel(user2, channel1);
        fileChannelService.printChannel(channel1);
        System.out.println();
        // 채널에 참여해 있던 유저가 해당 채널을 떠났을 때 해당 채널의 정보를 조회하여 성공적으로 유저가 채널을 빠져나갔는지 확인

        System.out.println("8. 채널 정보 수정, 채널 주인 변경, 권한 없는 유저가 수정하려 할 떄=================");
        fileChannelService.addUserToChannel(user2, channel1); // 위에서 퇴장했으니 다시 입장시켜서
        fileChannelService.updateHostUser(user2, channel1, user1);
        // 채널의 주인을 변경하려는 시도, 채널 주인이 아니면 채널의 주인을 다른 유저로 변경할 수 없다.
        fileChannelService.updateHostUser(user1, channel1, user2);
        // 채널의 주인을 변경
        fileChannelService.printChannel(channel1);
        System.out.println();
        // 주인이 변경된 채널을 조회

        System.out.println("9. 채널 삭제, 권한 없는 유저가 삭제하려 할 때=================");
        fileChannelService.deleteChannel(user1 ,channel1); // "권용진-1-서버" 채널 삭제
        System.out.println();
        // 주인이 아니면 채널을 삭제 할 수 없다.
        fileChannelService.printAllChannels();
        System.out.println();

        fileChannelService.deleteChannel(user2 ,channel1); // "권용진-1-서버" 채널 삭제
        System.out.println();
        // 채널 주인은 채널을 성공적으로 지울 수 있다.

        fileChannelService.printAllChannels();
        System.out.println();
        // 성공적으로 삭제되었는지를 확인하기위한 채널 전체 조회
    }

    public static void loadChannelTestApplication() {
        System.out.println("1. 유저 파일및 채널 파일 불러온 후 전체 유저 조회");
        UserService fileUserService = ServiceFactory.getInstance().getFileUserService();
        ChannelService fileChannelService = ServiceFactory.getInstance().getFileChannelService();

        fileUserService.printAllUsers();
        System.out.println();
        fileChannelService.printAllChannels();
        System.out.println();
    }

    public static void messageTestApplication() {
        UserService fileUserService = ServiceFactory.getInstance().getFileUserService();
        ChannelService fileChannelService = ServiceFactory.getInstance().getFileChannelService();
        MessageService fileMessageService = ServiceFactory.getInstance().getFileMessageService();

        System.out.println("1. 유저 생성=================");
        User user1 = fileUserService.createUser("권용진-1");
        User user2 = fileUserService.createUser("권용진-2");
        System.out.println(); // 가독성을 위한 개행
        // 메세지를 생성하기 위한 유저 생성
        System.out.println("2. 채널 생성=================");
        Channel channel1 = fileChannelService.createChannel(user1, "권용진-1의 채널1");
        Channel channel2 = fileChannelService.createChannel(user1, "권용진-1의 채널2");
        System.out.println(); // 가독성을 위한 개행
        // 메세지를 생성하기 위한 채널 생성
        System.out.println("3. 메세지 생성=================");
        Message message1 = fileMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지1 입니다"); // 메세지 등록
        Message message2 = fileMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지2 입니다"); // 메세지 등록
        Message message3 = fileMessageService.createMessage(user1, channel2, "채널2의 테스트 메세지1 입니다"); // 메세지 등록
        System.out.println();
        System.out.println("4. 유저 별 메세지 조회=================");
        fileMessageService.printAllMessageByUser(user1); //유저 별 조회
        System.out.println();
        System.out.println("5. 채널 별 메시지 조회=================");
        fileMessageService.printMessagesByChannel(channel1); // 채널 별 조회
        System.out.println();
        fileMessageService.printMessagesByChannel(channel2); // 채널 별 조회
        System.out.println();

        System.out.println("6. 메세지 단일 조회=================");
        fileMessageService.printMessage(message1);
        System.out.println();

        System.out.println("7. 메세지 전체 조회=================");
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("8. 메세지 정보 수정=================");
        fileMessageService.updateMessage(user1, message1, "채널 1의 테스트 메세지의 새로운 업데이트입니다.");
        System.out.println();
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("8. 메시지 주인이 아닌 유저가 메세지 삭제 하고자 할 때와 메시지 주인인 유저가 메시지 삭제할 떄=================");
        fileMessageService.deleteMessage(user2, message1);
        fileMessageService.deleteMessage(user1, message1);
        System.out.println();
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("9. 유저 삭제시 메시지도 동시 삭제=================");
        fileChannelService.addUserToChannel(user2, channel1);
        Message message4 = fileMessageService.createMessage(user2, channel1, "삭제 될 유저의 테스트 메시지 입니다."); // 메세지 등록
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();
        fileUserService.deleteUser(user2);
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("10. 삭제되어 비활성화 된 유저가 메시지를 생성하고자 할 때=================");
        fileMessageService.createMessage(user2, channel1, "삭제된 유저의 테스트 메시지입니다.");
        System.out.println();
        fileUserService.restoreUser(user2); // 이전 단게에서 user2는 삭제된 상태이므로 다시 활성화 상태로 바꿔준다
        fileMessageService.createMessage(user2, channel1, "복구된 유저의 테스트 메시지입니다.");
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("11. 유저 본인이 아닌 다른 유저가 메시지를 변경하고자 시도 했을 때=================");
        fileMessageService.updateMessage(user2, message1, "채널 1의 테스트 메세지의 새로운 업데이트입니다.");
        System.out.println(); // user2는 해당 메시지의 주인이 아니므로 메시지의 내용을 변경할 수 없다.
        fileMessageService.printAllMessage(); // 전체 조회
        System.out.println();
    }

    public static void loadMessageTestApplication() {
        System.out.println("1. 유저 파일 채널 파일및 메세지 파일을 불러온 후 전체 유저 조회");
        UserService fileUserService = ServiceFactory.getInstance().getFileUserService();
        ChannelService fileChannelService = ServiceFactory.getInstance().getFileChannelService();
        MessageService fileMessageService = ServiceFactory.getInstance().getFileMessageService();

        fileUserService.printAllUsers();
        System.out.println();
        fileChannelService.printAllChannels();
        System.out.println();
        fileMessageService.printAllMessage();
        System.out.println();
    }
}
