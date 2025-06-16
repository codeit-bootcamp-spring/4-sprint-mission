package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class TestJCFServiceApplication {

    public static void main(String[] args) {
        testJCFService(); // sprint1 때 만든 코드 내용들
        // sprint1 떄 만든 코드로써 저장 로직이 변경 되었으므로 data 폴던안의 파일들을 전부 삭제하면 돌아간다. -- sprint2 개발 중
    }

    public static void testJCFService() {
        System.out.println("\n========== userService Test start ==========================================\n");
        userTestApplication();
        System.out.println("\n========== userService Test end ==========================================\n");
        // userService 테스트

        System.out.println("\n========== channelService Test start ==========================================\n");
        channelTestApplication();
        System.out.println("\n========== channelService Test end ==========================================\n");
        // channelService 테스트

        System.out.println("\n========== messageService Test start ==========================================\n");
        messageTestApplication();
        System.out.println("\n========== messageService Test end ==========================================\n");
        // messageService 테스트
    }

    public static void userTestApplication() {

        UserService jcfUserService = ServiceFactory.getInstance().getUserService();
        
        System.out.println("1. 유저 등록=================");
        User user1 = jcfUserService.createUser("권용진-1");
        User user2 = jcfUserService.createUser("권용진-2");
        System.out.println(); // 가독성을 위한 줄바꾸
        // 두 명의 유저를 등록

        System.out.println("2. 단일 유저 조회=================");
        jcfUserService.printUser(user1);
        System.out.println();
        // 유저 한명의 정보를 단일 조회

        System.out.println("3. 전체 유저 조회=================");
        jcfUserService.printActiveUsers();
        System.out.println();
        // 유저 전체의 정보를 조회
        System.out.println("4. 유저 정보 변경=================");
        jcfUserService.updateUser(user1, "수정된 권용진-1");
        System.out.println();
        // 유저의 이름을 변경
        jcfUserService.printUser(user1);
        System.out.println();
        // 변경된 유저의 정보를 조회
        System.out.println("5. 유저 삭제(비활성화)=================");
        jcfUserService.deleteUser(user1); 
        System.out.println();
        // 유저 한 명을 삭제를 통해 비활성화
        jcfUserService.printActiveUsers(); // 조회
        System.out.println();
        // 정상적을 삭제 되었는지를 확인 하기 위한 활성화 상태인 유저 전체 조회
        jcfUserService.printAllUsers(); // 조회
        System.out.println();
        // 활성화된 유저와 비활성화된 유저 전체 조회
        jcfUserService.printDeactivatedUsers(); // 조회
        System.out.println();
        // 비활성화된 유저만 조회
        System.out.println("6. 유저 활성화===============");
        jcfUserService.restoreUser(user1);
        System.out.println();
        jcfUserService.printActiveUsers(); // 조회
        System.out.println();
        jcfUserService.printDeactivatedUsers(); // 조회
        System.out.println();
    }

    public static void channelTestApplication() {
        UserService jcfUserService = ServiceFactory.getInstance().getUserService();
        ChannelService jcfChannelService = ServiceFactory.getInstance().getChannelService();
        /*
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */
        System.out.println("1. 유저 생성=================");
        User user1 = jcfUserService.createUser("권용진-1");
        User user2 = jcfUserService.createUser("권용진-2");
        System.out.println();
        // 채널을 만들기 위한 유저 생성

        System.out.println("2. 채널 생성=================");
        Channel channel1 = jcfChannelService.createChannel(user1, "권용진-1-서버");
        Channel channel2 = jcfChannelService.createChannel(user2, "권용진-2-서버");
        System.out.println();
        // 만들어진 유저를 이용하여 채널 생성
        System.out.println();
        System.out.println("3. 채널 전체 조회=================");
        jcfChannelService.printAllChannels();
        System.out.println();
        // 생성된 채널 전체 조회
        System.out.println("4. 채널 단일 조회=================");
        jcfChannelService.printChannel(channel1);
        System.out.println();
        // 생성된 채널 단일 조회

        System.out.println("5. 채널 정보 수정, 유저 채널 입장, 동일 유저가 중복 입장하려 할 때=================");
        jcfChannelService.addUserToChannel(user2, channel1); // user2 번이 1번 채널에 추가됨
        // 이미 만들어진 채널에 다른 유저가 입장
        jcfChannelService.addUserToChannel(user2, channel1); // user2 번이 1번 채널에 추가됨
        System.out.println();
        jcfChannelService.printUsersFromChannel(channel1);
        System.out.println();

        // 유저가 새로이 입장한 채널의 정보를 조회하여 성공적으로 유저가 입장했는지 확인

        System.out.println("6. 채널 정보 수정, 채널 이름 수정, 권한 없는 유저가 수정하려 할 떄=================");
        jcfChannelService.updateChannelName(user2, channel1, "권용진-1-서버의 새이름");
        // 채널 주인인 유저만 채널의 이름을 변경할 수 있음
        jcfChannelService.printChannel(channel1);
        System.out.println();
        // 채널의 정보가 안변했음을 확인하기 위해 채널 정보 조회
        
        jcfChannelService.updateChannelName(user1, channel1, "권용진-1-서버의 새이름");// 채널 이름 변경
        System.out.println();
        // 채널 주인은 채널의 이름을 변경할 수 있음
        
        jcfChannelService.printAllChannels();
        System.out.println();
        // 변경된 채널 이름 조회

        System.out.println("7. 채널 정보 수정, 채널 내 유저 퇴장=================");
        jcfChannelService.leaveUserFromChannel(user2, channel1);
        jcfChannelService.printChannel(channel1);
        System.out.println();
        // 채널에 참여해 있던 유저가 해당 채널을 떠났을 때 해당 채널의 정보를 조회하여 성공적으로 유저가 채널을 빠져나갔는지 확인

        System.out.println("8. 채널 정보 수정, 채널 주인 변경, 권한 없는 유저가 수정하려 할 떄=================");
        jcfChannelService.addUserToChannel(user2, channel1); // 위에서 퇴장했으니 다시 입장시켜서
        jcfChannelService.updateHostUser(user2, channel1, user1);
        // 채널의 주인을 변경하려는 시도, 채널 주인이 아니면 채널의 주인을 다른 유저로 변경할 수 없다.
        jcfChannelService.updateHostUser(user1, channel1, user2);
        // 채널의 주인을 변경
        jcfChannelService.printChannel(channel1);
        System.out.println();
        // 주인이 변경된 채널을 조회

        System.out.println("9. 채널 삭제, 권한 없는 유저가 삭제하려 할 때=================");
        jcfChannelService.deleteChannel(user1 ,channel1); // "권용진-1-서버" 채널 삭제
        System.out.println();
        // 주인이 아니면 채널을 삭제 할 수 없다.
        jcfChannelService.printAllChannels();
        System.out.println();

        jcfChannelService.deleteChannel(user2 ,channel1); // "권용진-1-서버" 채널 삭제
        System.out.println();
        // 채널 주인은 채널을 성공적으로 지울 수 있다.

        jcfChannelService.printAllChannels();
        System.out.println();
        // 성공적으로 삭제되었는지를 확인하기위한 채널 전체 조회
    }

    public static void messageTestApplication() {
        UserService jcfUserService = ServiceFactory.getInstance().getUserService();
        ChannelService jcfChannelService = ServiceFactory.getInstance().getChannelService();
        MessageService jcfMessageService = ServiceFactory.getInstance().getMessageService();
        /*
        [ ] 등록
        [ ] 조회(단건, 다건)
        [ ] 수정, 의존성
        [ ] 수정된 데이터 조회
        [ ] 삭제
        [ ] 조회를 통해 삭제되었는지 확인
        */

        System.out.println("1. 유저 생성=================");
        User user1 = jcfUserService.createUser("권용진-1");
        User user2 = jcfUserService.createUser("권용진-2");
        System.out.println(); // 가독성을 위한 개행
        // 메세지를 생성하기 위한 유저 생성
        System.out.println("2. 채널 생성=================");
        Channel channel1 = jcfChannelService.createChannel(user1, "권용진-1의 채널1");
        Channel channel2 = jcfChannelService.createChannel(user1, "권용진-1의 채널2");
        System.out.println(); // 가독성을 위한 개행
        // 메세지를 생성하기 위한 채널 생성
        System.out.println("3. 메세지 생성=================");
        Message message1 = jcfMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지1 입니다"); // 메세지 등록
        Message message2 = jcfMessageService.createMessage(user1, channel1, "채널1의 테스트 메세지2 입니다"); // 메세지 등록
        Message message3 = jcfMessageService.createMessage(user1, channel2, "채널2의 테스트 메세지1 입니다"); // 메세지 등록
        System.out.println();
        System.out.println("4. 유저 별 메세지 조회=================");
        jcfMessageService.printAllMessageByUser(user1); //유저 별 조회
        System.out.println();
        System.out.println("5. 채널 별 메시지 조회=================");
        jcfMessageService.printMessagesByChannel(channel1); // 채널 별 조회
        System.out.println();
        jcfMessageService.printMessagesByChannel(channel2); // 채널 별 조회
        System.out.println();

        System.out.println("6. 메세지 단일 조회=================");
        jcfMessageService.printMessage(message1);
        System.out.println();

        System.out.println("7. 메세지 전체 조회=================");
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("8. 메세지 정보 수정=================");
        jcfMessageService.updateMessage(user1, message1, "채널 1의 테스트 메세지의 새로운 업데이트입니다.");
        System.out.println();
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("8. 메시지 주인이 아닌 유저가 메세지 삭제 하고자 할 때와 메시지 주인인 유저가 메시지 삭제할 떄=================");
        jcfMessageService.deleteMessage(user2, message1); 
        jcfMessageService.deleteMessage(user1, message1);
        System.out.println();
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("9. 유저 삭제시 메시지도 동시 삭제=================");
        jcfChannelService.addUserToChannel(user2, channel1);
        Message message4 = jcfMessageService.createMessage(user2, channel1, "삭제 될 유저의 테스트 메시지 입니다."); // 메세지 등록
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();
        jcfUserService.deleteUser(user2);
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("10. 삭제되어 비활성화 된 유저가 메시지를 생성하고자 할 때=================");
        jcfMessageService.createMessage(user2, channel1, "삭제된 유저의 테스트 메시지입니다.");
        System.out.println();
        jcfUserService.restoreUser(user2); // 이전 단게에서 user2는 삭제된 상태이므로 다시 활성화 상태로 바꿔준다
        jcfMessageService.createMessage(user2, channel1, "복구된 유저의 테스트 메시지입니다.");
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();

        System.out.println("11. 유저 본인이 아닌 다른 유저가 메시지를 변경하고자 시도 했을 때=================");
        jcfMessageService.updateMessage(user2, message1, "채널 1의 테스트 메세지의 새로운 업데이트입니다.");
        System.out.println(); // user2는 해당 메시지의 주인이 아니므로 메시지의 내용을 변경할 수 없다.
        jcfMessageService.printAllMessage(); // 전체 조회
        System.out.println();
    }
}
