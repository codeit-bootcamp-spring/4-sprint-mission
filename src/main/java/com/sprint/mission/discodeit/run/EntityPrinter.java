package com.sprint.mission.discodeit.run;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public class EntityPrinter {
    // 프린트 메서드
    public enum PrintCode {
        USER, CHANNEL, MESSAGE
    }

    // 채널 프린트 메서드
    public static void printService(Channel c, PrintCode printCode) {
        switch (printCode) {
            case USER ->
                    System.out.println("<\"" + c.getChannelName() + "\" (" + c.getId() + ")에 참가한 유저 목록 조회하기>\n" + c.getUsers());
            case CHANNEL -> System.out.println("<\"" + c.getChannelName() + "\" (" + c.getId() + ")의 상태 확인하기>\n" + c);
            case MESSAGE ->
                    System.out.println("<\"" + c.getChannelName() + "\" (" + c.getId() + ")에 보내진 메시지 목록 조회하기>\n" + c.getMessages());

            default -> System.out.println("<실패: 출력 실패, 잘못된 입력값 입니다.>");
        }
    }

    // 유저 프린트 메서드
    public static void printService(User u, PrintCode printCode) {
        switch (printCode) {
            case USER ->
                    System.out.println("<\"" + u.getUserName() + "\"" + "님(" + u.getId() + ")" + "의 상태 확인하기>\n" + u);
            case CHANNEL ->
                    System.out.println("<\"" + u.getUserName() + "\"" + "님(" + u.getId() + ")" + "이 참여한 채널 목록>\n" + u.getChannels());
            case MESSAGE ->
                    System.out.println("<\"" + u.getUserName() + "\"" + "님(" + u.getId() + ")" + "이 보낸 메시지 목록>\n" + u.getMessages());

            default -> System.out.println("<실패: 출력 실패, 잘못된 입력값 입니다.>");
        }
    }

    // 메시지 프린트 메서드
    public static void printService(Message m, PrintCode printCode) {
        switch (printCode) {
            case USER ->
                    System.out.println("<이 메시지는 \"" + m.getUser().getUserName() + "\"(" + m.getUser().getId() + ")님이 보내신 메시지 입니다.> \n" + m);

            case CHANNEL ->
                    System.out.println("<이 메시지는 \"" + m.getChannel().getChannelName() + "\"(" + m.getChannel().getId() + ")에서 보내진 메시지 입니다.> \n" + m);
            case MESSAGE ->
                    System.out.println("<메시지 \"" + m.getMessageContents() + "\"(" + m.getId() + ") 의 상태 확인하기>\n" + m);

            default -> System.out.println("<실패: 출력 실패, 잘못된 입력값 입니다.>");
        }
    }
}
