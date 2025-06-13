package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.ServiceFactory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Optional;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.

public class JavaApplication extends EntityPrinter {

    private static ChannelService channelService;
    private static UserService userService;
    private static MessageService messageService;

    public static void main(String[] args) {

        // 테스트 코드
        // ServiceFactory
        ServiceFactory factory = new ServiceFactory();

        // JCFUserService
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<유저 서비스에 대한 테스트 코드를 시작합니다>");
        userService = factory.getUserService();

        // Create
        User user1 = userService.addUser("이지현").orElse(userService.temp());
        User user2 = userService.addUser("꾸미").orElse(userService.temp());
        User user3 = userService.addUser("이지현").orElse(userService.temp());

        // Delete
        userService.deleteUserById(user3.getId());
        // delete - error
        //userService.deleteUserById(user3.getId());

        // Update
        userService.updateUserById(user2.getId(), "꾸미공주");
        // update - error
        //userService.updateUser(user3.getId(), "이지현2");

        // Read
        System.out.println(userService.getUsers());
        System.out.println(userService.getUserById(user2.getId()));
        // read - error
        // System.out.println(userService.getUsersById(user3.getId()));


        // JCFChannelService
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<채널 서비스에 대한 테스트 코드를 시작합니다.>");
        // ChannelService channelService = factory.getChannelService();
        channelService = factory.getChannelService();

        // Create
        // addChannel: Optional<Channel> 반환. 조건에 걸린 경우 empty 상태
        Optional<Channel> channel1 = channelService.addChannel("집", user1);
        Optional<Channel> channel2 = channelService.addChannel("밥", user2);
        Optional<Channel> channel3 = channelService.addChannel("코드잇", user1);

        // Delete
        channel2.ifPresent(c -> channelService.deleteChannelById(c.getId(), user2.getId()));
        // delete - error
        // channel2.ifPresent(c -> channelService.deleteChannelById(c.getId()));

        // Update
        channel1.ifPresent(c -> channelService.updateChannelNameById(c.getId(), user1.getId(), "우리집"));
        // update - error
        // channel1.ifPresent(c -> channelService.updateChannelNameById(c.getId(), user2.getId(), "우리집"));

        // Read
        System.out.println(channelService.getChannels());
        channel3.ifPresent(c -> System.out.println(channelService.getChannelById(c.getId())));
        // read - error
        // channel4.ifPresent(c -> System.out.println(channelService.getChannelById(c.getId())));


        // JCFMessageService
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<메시지 서비스에 대한 테스트 코드를 시작합니다.>");
        messageService = factory.getMessageService();

        // Create
        // 매개변수가 유효하지 않으면 Optional.empty()가 반환됨.
        Optional<Message> message1 = channel1.flatMap(c -> messageService.addMessage(user1, c, "미역국을 끓여야지."));
        Optional<Message> message3 = channel3.flatMap(c -> messageService.addMessage(user1, c, "공부를 해야지."));

        // create - error: 가입되지 않은 채널에는 메시지 생성 불가
        Optional<Message> message2 = channel3.flatMap(c -> messageService.addMessage(user2, c, "밥 먹어야지."));

        // Delete
        message1.ifPresent(m -> messageService.deleteMessageById(m.getId(), user1, channel1.get()));  // message1 삭제
        // delete - error
        // message1.ifPresent(m -> messageService.deleteMessageById(m.getId()));

        // Update
        message3.ifPresent(m -> messageService.updateMessageContentsById(m.getId(), user1, "쉬는시간!!"));
        // update - error: 삭제된 메시지는 수정 불가
        message1.ifPresent(m -> messageService.updateMessageContentsById(m.getId(), user1, "화장실에 갔다 와야 겠다."));
        // update - error: 메시지 작성자가 아니면 수정 불가
        message3.ifPresent(m -> messageService.updateMessageContentsById(m.getId(), user2, "공부 그만해"));

        // Read
        System.out.println(messageService.getMessages());
        message3.ifPresent(m -> messageService.getMessageById(m.getId()));
        // read - error: 삭제된 아이디(리스트에 존재하지 않는 아이디)
        message1.ifPresent(m -> messageService.getMessageById(m.getId()));


        // 동시성 연결 테스트 코드
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<동시성 연결에 대한 테스트 코드 테스트 코드를 시작합니다.>");
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<동시성 테스트: 채널 참가/나가기>\n");
        // 채널 참가하기
        channel1.ifPresent(c -> userService.joinChannel(user2.getId(), c));
        // error: 이미 참가한 유저는 다시 참가 못함.
        channel1.ifPresent(c -> userService.joinChannel(user2.getId(), c));
        printService(user1, PrintCode.CHANNEL);
        printService(user1, PrintCode.MESSAGE);

        // 채널 나가기
        channel1.ifPresent(c -> userService.outChannel(user2.getId(), c));
        printService(user1, PrintCode.CHANNEL);
        printService(user1, PrintCode.MESSAGE);

        // 채널 다시 참가하기
        channel1.ifPresent(c -> userService.joinChannel(user2.getId(), c));

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("\n<동시성 테스트: create>");
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("\n<메시지 생성>");
        // 메시지 생성 테스트
        Optional<Message> message4 = channel1.flatMap(c -> messageService.addMessage(user2, c, "밥을 줘라"));
        Optional<Message> message5 = channel1.flatMap(c -> messageService.addMessage(user1, channel1.get(), "드리겠습니다."));

        printService(user2, PrintCode.CHANNEL);
        printService(user2, PrintCode.MESSAGE);

        channel1.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        channel1.ifPresent(c -> printService(c, PrintCode.USER));

        message4.ifPresent(m -> printService(m, PrintCode.MESSAGE));

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<동시성 테스트: update>\n");
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");

        // 메시지 update 후 다시 조회해보기.
        System.out.println("\n<메시지 수정>");
        message4.ifPresent(m -> messageService.updateMessageContentsById(m.getId(), user2, "음 잘 먹었다!!!"));
        printService(user2, PrintCode.MESSAGE);
        channel1.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        message4.ifPresent(m -> printService(m, PrintCode.MESSAGE));

        System.out.println("<채널, 유저 이름 변경하기>\n");
        System.out.println("\n<채널 이름 수정>");
        channel1.ifPresent(c -> channelService.updateChannelNameById(c.getId(), user1.getId(), "우리집 최고"));
        printService(user1, PrintCode.CHANNEL);
        printService(user2, PrintCode.CHANNEL);

        System.out.println("\n<유저 이름 수정>");
        userService.updateUserById(user1.getId(), "캔따개");
        channel1.ifPresent(c -> printService(c, PrintCode.USER));

        // delete 테스트 전, 테스트 데이터 생성
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("<동시성 테스트: delete>\n");
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("\n<delete용 테스트 데이터 생성>");
        // user1(캔따개)가 카페 채널 생성
        Optional<Channel> channel4 = channelService.addChannel("카페", user1);
        // user1(캔따개)가 메시지 생성(message6, message7)
        Optional<Message> message6 = channel4.flatMap(c -> messageService.addMessage(user1, c, "아이스 아메리카노 1잔 주세요."));
        Optional<Message> message7 = channel4.flatMap(c -> messageService.addMessage(user1, c, "크로플도 1개 주세요."));

        // user4(김코드) 생성, channel4(카페) 참가, message8 생성
        User user4 = userService.addUser("김코드").orElse(userService.temp());
        channel4.ifPresent(c -> userService.joinChannel(user4.getId(), c));
        Optional<Message> message8 = channel4.flatMap(c -> messageService.addMessage(user4, c, "따뜻한 아메리카노 1잔 주세요."));

        // channel4, user1, user4의 메시지 조회
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        printService(user1, PrintCode.MESSAGE);
        printService(user4, PrintCode.MESSAGE);

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // message6 삭제(user1이 작성한 아아1잔 주문)
        System.out.println("\n<메시지 삭제>");
        message6.ifPresent(m -> messageService.deleteMessageById(m.getId(), user1, channel4.get()));
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        printService(user4, PrintCode.MESSAGE);

        //delete - error: message8을 user1이 삭제하는 건 불가능. (message8을 작성한 user4(김코드)만 삭제 가능)
        System.out.println("\n<메시지 삭제 에러 테스트>");
        message8.ifPresent(m -> messageService.deleteMessageById(m.getId(), user1, channel4.get()));  // 에러: 유저가 다르면 삭제 불가
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // user4(김코드) 삭제, Status == QUIT
        System.out.println("\n<유저 삭제 후, 채널에서 유저와 메시지 목록 조회해보기>");
        userService.deleteUserById(user4.getId());
        channel4.ifPresent(c -> printService(c, PrintCode.USER));
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));

        // 채널 참가/퇴장 심화 테스트: 메시지가 함께 삭제되는지 확인(채널에서 나가면 그 사람이 보낸 메시지도 함께 삭제됨)
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("\n<채널 참가/퇴장, 메시지 삭제 확인 (joinChannel, outChannel)>");
        // user2(꾸미 공주)가 channel4(카페)에 참가
        System.out.println("<채널 참가>");
        channel4.ifPresent(c -> userService.joinChannel(user2.getId(), c));
        channel4.ifPresent(c -> printService(c, PrintCode.USER));
        printService(user2, PrintCode.CHANNEL);

        // user2(꾸미 공주)가 message9를 channel4(카페)에 생성
        System.out.println("\n<채널 참가자 메시지 전송>");
        Optional<Message> message9 = channel4.flatMap(c -> messageService.addMessage(user2, c, "여기서 뭐 하고 있냐"));
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        printService(user2, PrintCode.MESSAGE);

        // user2(꾸미 공주)가 channel4(카페)에서 퇴장
        System.out.println("\n <채널 나가기, 동기화 확인>");
        channel4.ifPresent(c -> userService.outChannel(user2.getId(), c));
        // channel4(카페)에 user2(꾸미 공주) 양측에 정보가 남았는지 확인(삭제 되어야 함)
        channel4.ifPresent(c -> printService(c, PrintCode.USER));
        printService(user2, PrintCode.CHANNEL);
        channel4.ifPresent(c -> printService(c, PrintCode.MESSAGE));
        printService(user2, PrintCode.MESSAGE);

        // channel4(카페) 삭제 후 카페에 가입되어 있던 유저의 데이터 확인(삭제 되어야 함)
        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        System.out.println("\n <채널 삭제>");
        channel4.ifPresent(c -> channelService.deleteChannelById(c.getId(), user1.getId()));
        printService(user1, PrintCode.CHANNEL);
        printService(user1, PrintCode.MESSAGE);
        System.out.println(channelService.getChannels());

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // 활성 기능 테스트 코드
        System.out.println("\n<활성 기능 테스트>");
        User user5 = userService.addUser("").orElse(userService.temp());

        // error: 비활성화된 채널은 참가 불가, 메시지 전송도 불가
        System.out.println("\n<비활성 채널 에러 테스트>");
        channel4.ifPresent(c -> userService.joinChannel(user5.getId(), c));
        channel4.ifPresent(c -> userService.outChannel(user5.getId(), c));

        Optional<Message> message11 = channel4.flatMap(c -> messageService.addMessage(user5, c, "비활성 채널 메시지 테스트"));

        // error: 비활성화된 회원은 채널 참가, 메시지 작성 등 불가 (BANNED, SLEEP, QUIT)
        System.out.println("\n<비활성 회원 에러 테스트>");
        userService.updateUserStatusById(user5.getId(), User.Status.SLEEP);
        userService.updateUserById(user5.getId(), "이불");

        channel1.ifPresent(c -> userService.joinChannel(user5.getId(), c));
        Optional<Message> message12 = channel1.flatMap(c -> messageService.addMessage(user5, c, "쿨쿨"));

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // 활성화 기능 테스트
        // user5: SLEEP(휴면) -> ACTIVE(활성)
        System.out.println("\n<Activate 기능 테스트>");
        userService.activateUserById(user5.getId());
        System.out.println("user5(\"" + user5.getUserName() + "\")의 상태: " + user5.getStatus());

        // error: 탈퇴한 회원, 이미 활성화된 회원은 재활성화 불가능. user4(김코드): QUIT, user1(이지현): ACTIVE
        System.out.println("\n<Activate 기능 에러 테스트>");
        userService.activateUserById(user4.getId());
        userService.activateUserById(user1.getId());

        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // 탈퇴 회원(QUIT) 테스트
        // error: 탈퇴한 회원은 채널을 생성하거나, 메시지를 전송할 수 없음. user4(김코드): QUIT
        System.out.println("\n<탈퇴 회원(QUIT) 테스트>");
        Optional<Channel> channel5 = channelService.addChannel("김코드님의 채널", user4);
        Optional<Message> message10 = channel5.flatMap(c -> messageService.addMessage(user4, c, "신나는 음악을 들어요"));

        // 추가 안됨(채널, 메시지) (channel5(김코드님의 채널), message10(신나는 음악을 들어요))
        System.out.println("<channel5(김코드님의 채널) 추가 실패>\n" + channelService.getChannels() + "\n");
        System.out.println("<message10(신나는 음악을 들어요)이 추가 실패>\n" + messageService.getMessages());


        System.out.println("\n////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////\n");
        // user6(guest)의 Status가 deactivateUserById()로 인해 SLEEP으로 전환됨.
        System.out.println("\n<enum 타입 테스트: deactivateUserById() 테스트>");
        User user6 = userService.addUser("").orElse(userService.temp());
        printService(user6, PrintCode.USER);

        userService.deactivateUserById(user6.getId());
        System.out.println("user6(\"" + user6.getUserName() + "\")의 상태: " + user6.getStatus());

        // error: ACTIVE가 아닌 회원(BANNED, SLEEP, QUIT)은 채널 참가, 유저 정보 업데이트 등 서비스 이용 불가능
        //channel1.ifPresent(c -> userService.joinChannel(user6.getId(), c));
        userService.updateUserById(user6.getId(), "킹콩부대찌개");
        System.out.println(userService.getUserById(user6.getId()));
    }
}