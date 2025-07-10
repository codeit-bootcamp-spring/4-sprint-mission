package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.config.RepositorySettings;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableConfigurationProperties(RepositorySettings.class)
public class DiscodeitApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

		ChannelService channelService = context.getBean(ChannelService.class);
		UserService userService = context.getBean(UserService.class);
		MessageService messageService = context.getBean(MessageService.class);
		AuthService authService = context.getBean(AuthService.class);
		BinaryContentService binaryContentService = context.getBean(BinaryContentService.class);
		ReadStatusService readStatusService = context.getBean(ReadStatusService.class);
		UserStatusService userStatusService = context.getBean(UserStatusService.class);


		// =============================== 테스트 전 모든 데이터 초기화 ===============================
		clearAllData(channelService, userService, messageService, binaryContentService, readStatusService, userStatusService);

	}

	/**
	 * 테스트 하기 전 모든 데이터 초기화합니다.
	 */
	private static void clearAllData(ChannelService channelService, UserService userService, MessageService messageService, BinaryContentService binaryContentService, ReadStatusService readStatusService, UserStatusService userStatusService) {
		channelService.deleteAll();
		userService.deleteAll();
		messageService.deleteAll();
		binaryContentService.deleteAll();
		readStatusService.deleteAll();
		userStatusService.deleteAll();
	}
}
