package com.sprint.mission.discodeit;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DiscodeitApplication {

//	static User setupUser(UserService userService) {
//		User user = userService.createUser("블러퍼","123");
//		return user;
//	}
//
//	static Channel setupChannel(ChannelService channelService) {
//		Channel channel = channelService.createChannel("대화방");
//		return channel;
//	}
//
//	static void messageCreate(MessageService messageService) {
//		Message message = messageService.createMessage("안녕하세요");
//		System.out.println("메세지 생성:" + message.getId());
//	}
//
//	public static void main(String[] args) {
//		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args); // 얘가 spring 컨테이너다
//		// 서비스 초기화
//
//		UserService userService = context.getBean(UserService.class); // 스프링 컨테이너에 등록된 빈을 불러옴
//		userService.createUser("신동진","123123");
//
//		ChannelService channelService = context.getBean(ChannelService.class); // 인터페이스 ChannelService에 포함되는 객체를 spring 컨테이너에서 찾아옴
//		// Bean은 SingleTon이라서 단일 객체, 중복 불가니까 객체 들 아님 객체를 가져옴
//
//		// TODO context에서 Bean을 조회하여 각 서비스 구현체 할당 코드 작성하세요.
//		// UserService userService;
//		// ChannelService channelService;
//		MessageService messageService;
//		// 얘네가 빈이면 35~37번을 31번 라인에서 불러와야한다
//
//
//		// ...
//	}
}
