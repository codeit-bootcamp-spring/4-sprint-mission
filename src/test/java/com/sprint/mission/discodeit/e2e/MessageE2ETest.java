package com.sprint.mission.discodeit.e2e;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.response.PageResponse;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class MessageE2ETest {
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private ReadStatusRepository readStatusRepository;

    @BeforeEach
    void setUp() {
        var httpClient = org.apache.hc.client5.http.impl.classic.HttpClients.createDefault();
        var factory = new org.springframework.http.client.HttpComponentsClientHttpRequestFactory(httpClient);
        rest.getRestTemplate().setRequestFactory(factory);
    }

    @AfterTransaction
    void cleanUp() {
        readStatusRepository.deleteAll();
        messageRepository.deleteAll();
        userRepository.deleteAll();
        channelRepository.deleteAll();
    }

    @DisplayName("메시지 생성")
    @Test
    void create() {
        // given 유저
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> UserRequest = new LinkedMultiValueMap<>();
        UserRequest.add("userCreateRequest", userCreateRequest);
        UserRequest.add("profile", null);

        // when POST /api/users
        ResponseEntity<UserDto> UserCreated = rest.postForEntity("/api/users", UserRequest, UserDto.class);

        // then - 201 응답 + id 추가 확인
        assertThat(UserCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(UserCreated.getBody()).isNotNull();
        UUID userId = UserCreated.getBody().id();
        assertThat(userId).isNotNull();

        // given 채널
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // when - POST /api/channel/public
        ResponseEntity<ChannelDto> channelCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(channelCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(channelCreated.getBody()).isNotNull();
        assertThat(channelCreated.getBody().description()).isEqualTo("description");
        assertThat(channelCreated.getBody().name()).isEqualTo("channelName");
        assertThat(channelCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = channelCreated.getBody().id();
        assertThat(channelId).isNotNull();

        // given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(userId, channelId, "content");
        List<MultipartFile> attachments = null;
        MultiValueMap<String, Object> request = new LinkedMultiValueMap<>();
        request.add("messageCreateRequest", messageCreateRequest);
        request.add("attachments", attachments);

        // when - POST /api/messages
        ResponseEntity<MessageDto> created = rest.postForEntity("/api/messages", request, MessageDto.class);

        // then - 201 응답 + id 추가 확인 + 내용 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        assertThat(created.getBody().id()).isNotNull();
        assertThat(created.getBody().channelId()).isEqualTo(channelId);
        assertThat(created.getBody().author().id()).isEqualTo(userId);
        assertThat(created.getBody().content()).isEqualTo("content");
    }

    @DisplayName("메시지 수정")
    @Test
    void update() {
        // given 유저
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> UserRequest = new LinkedMultiValueMap<>();
        UserRequest.add("userCreateRequest", userCreateRequest);
        UserRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> UserCreated = rest.postForEntity("/api/users", UserRequest, UserDto.class);

        // then - 201 + id 추가 확인
        assertThat(UserCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(UserCreated.getBody()).isNotNull();
        UUID userId = UserCreated.getBody().id();
        assertThat(userId).isNotNull();

        // and given 채널
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // and when - POST /api/channel/public
        ResponseEntity<ChannelDto> channelCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(channelCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(channelCreated.getBody()).isNotNull();
        assertThat(channelCreated.getBody().description()).isEqualTo("description");
        assertThat(channelCreated.getBody().name()).isEqualTo("channelName");
        assertThat(channelCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = channelCreated.getBody().id();
        assertThat(channelId).isNotNull();

        // and given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(userId, channelId, "content");
        List<MultipartFile> attachments = null;
        MultiValueMap<String, Object> createRequest = new LinkedMultiValueMap<>();
        createRequest.add("messageCreateRequest", messageCreateRequest);
        createRequest.add("attachments", attachments);

        // and when - POST /api/messages
        ResponseEntity<MessageDto> created = rest.postForEntity("/api/messages", createRequest, MessageDto.class);

        // and then - 201 응답 + id 추가 확인 + 내용 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID messageId = created.getBody().id();
        assertThat(messageId).isNotNull();
        assertThat(created.getBody().channelId()).isEqualTo(channelId);
        assertThat(created.getBody().author().id()).isEqualTo(userId);
        assertThat(created.getBody().content()).isEqualTo("content");

        // and given 수정 정보
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");
        HttpEntity<MessageUpdateRequest> updateRequest = new HttpEntity<>(messageUpdateRequest);

        // and when - PATCH /api/messages/{message-id}
        ResponseEntity<MessageDto> updated = rest.exchange("/api/messages/" + messageId, HttpMethod.PATCH, updateRequest, MessageDto.class);

        // and then - 200 응답 + id 확인 + 내용 확인
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().id()).isEqualTo(messageId);
        assertThat(updated.getBody().channelId()).isEqualTo(channelId);
        assertThat(updated.getBody().author().id()).isEqualTo(userId);
        assertThat(updated.getBody().content()).isEqualTo("newContent");
    }

    @DisplayName("메시지 삭제")
    @Test
    void delete() {
        // given 유저
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> UserRequest = new LinkedMultiValueMap<>();
        UserRequest.add("userCreateRequest", userCreateRequest);
        UserRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> UserCreated = rest.postForEntity("/api/users", UserRequest, UserDto.class);

        // then - 201 + id 추가 확인
        assertThat(UserCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(UserCreated.getBody()).isNotNull();
        UUID userId = UserCreated.getBody().id();
        assertThat(userId).isNotNull();

        // and given 채널
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // and when - POST /api/channel/public
        ResponseEntity<ChannelDto> channelCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(channelCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(channelCreated.getBody()).isNotNull();
        assertThat(channelCreated.getBody().description()).isEqualTo("description");
        assertThat(channelCreated.getBody().name()).isEqualTo("channelName");
        assertThat(channelCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = channelCreated.getBody().id();
        assertThat(channelId).isNotNull();

        // and given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(userId, channelId, "content");
        List<MultipartFile> attachments = null;
        MultiValueMap<String, Object> createRequest = new LinkedMultiValueMap<>();
        createRequest.add("messageCreateRequest", messageCreateRequest);
        createRequest.add("attachments", attachments);

        // when - POST /api/messages
        ResponseEntity<MessageDto> created = rest.postForEntity("/api/messages", createRequest, MessageDto.class);

        // then - 201 응답 + id 추가 확인 + 내용 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID messageId = created.getBody().id();
        assertThat(messageId).isNotNull();
        assertThat(created.getBody().channelId()).isEqualTo(channelId);
        assertThat(created.getBody().author().id()).isEqualTo(userId);
        assertThat(created.getBody().content()).isEqualTo("content");

        // and when & then - 203 응답 확인 + 바디 없음 확인
        ResponseEntity deleted = rest.exchange("/api/messages/" + messageId, HttpMethod.DELETE, HttpEntity.EMPTY, MessageDto.class);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(deleted.getBody()).isNull();
    }

    @DisplayName("채널에 해당하는 메시지 목록 조회")
    @Test
    void findAllMessage() {
        // given 유저
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> UserRequest = new LinkedMultiValueMap<>();
        UserRequest.add("userCreateRequest", userCreateRequest);
        UserRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> UserCreated = rest.postForEntity("/api/users", UserRequest, UserDto.class);

        // then - 201 + id 추가 확인
        assertThat(UserCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(UserCreated.getBody()).isNotNull();
        UUID userId = UserCreated.getBody().id();
        assertThat(userId).isNotNull();

        // and given 채널
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // and when - POST /api/channel/public
        ResponseEntity<ChannelDto> channelCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(channelCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(channelCreated.getBody()).isNotNull();
        assertThat(channelCreated.getBody().description()).isEqualTo("description");
        assertThat(channelCreated.getBody().name()).isEqualTo("channelName");
        assertThat(channelCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = channelCreated.getBody().id();
        assertThat(channelId).isNotNull();

        // and given
        MessageCreateRequest messageCreateRequest = new MessageCreateRequest(userId, channelId, "content");
        List<MultipartFile> attachments = null;
        MultiValueMap<String, Object> createRequest = new LinkedMultiValueMap<>();
        createRequest.add("messageCreateRequest", messageCreateRequest);
        createRequest.add("attachments", attachments);

        // when - POST /api/messages
        ResponseEntity<MessageDto> created = rest.postForEntity("/api/messages", createRequest, MessageDto.class);

        // then - 201 응답 + id 추가 확인 + 내용 확인
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(created.getBody()).isNotNull();
        UUID messageId = created.getBody().id();
        assertThat(messageId).isNotNull();
        assertThat(created.getBody().channelId()).isEqualTo(channelId);
        assertThat(created.getBody().author().id()).isEqualTo(userId);
        assertThat(created.getBody().content()).isEqualTo("content");

        // when - GET /api/messages?channelId={channel-id} 채널 아이디로 해당 채널에 모든 메시지를 페이지네이션하여 PageResponse로 반환
        ResponseEntity<PageResponse> response = rest.getForEntity("/api/messages?channelId=" + channelId, PageResponse.class);

        // then - 200 응답 + 전체 메시지 개수 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalElements()).isEqualTo(1);
    }

    @DisplayName("메시지는 알 수 없는 유저나 알 수 없는 채널에서 발생한 경우 실패한다.")
    @Test
    void createShouldFailedWhenUnknownUserOrUnknownChannel() {
        // given 유저
        UserCreateRequest userCreateRequest = new UserCreateRequest("username", "password", "test@email.com");
        MultiValueMap<String, Object> UserRequest = new LinkedMultiValueMap<>();
        UserRequest.add("userCreateRequest", userCreateRequest);
        UserRequest.add("profile", null);

        // when - POST /api/users
        ResponseEntity<UserDto> UserCreated = rest.postForEntity("/api/users", UserRequest, UserDto.class);

        // then - 201 + id 추가 확인
        assertThat(UserCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(UserCreated.getBody()).isNotNull();
        UUID userId = UserCreated.getBody().id();
        assertThat(userId).isNotNull();

        // and given 채널
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // and when - POST /api/channel/public
        ResponseEntity<ChannelDto> channelCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(channelCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(channelCreated.getBody()).isNotNull();
        assertThat(channelCreated.getBody().description()).isEqualTo("description");
        assertThat(channelCreated.getBody().name()).isEqualTo("channelName");
        assertThat(channelCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = channelCreated.getBody().id();
        assertThat(channelId).isNotNull();

        // and given 알 수 없는 유저 & 알 수 없는 채널
        MessageCreateRequest unknownUserMessageCreateRequest = new MessageCreateRequest(UUID.randomUUID(), channelId, "content");
        MultiValueMap<String, Object> badRequestUnknownUser = new LinkedMultiValueMap<>();
        badRequestUnknownUser.add("messageCreateRequest", unknownUserMessageCreateRequest);

        MessageCreateRequest unknownChannelMessageCreateRequest = new MessageCreateRequest(userId, UUID.randomUUID(), "content");
        MultiValueMap<String, Object> badRequestUnknownChannel = new LinkedMultiValueMap<>();
        badRequestUnknownChannel.add("messageCreateRequest", unknownChannelMessageCreateRequest);

        // when - POST /api/messages
        ResponseEntity<ErrorResponse> failCreatedUnknownUser = rest.postForEntity("/api/messages", badRequestUnknownUser, ErrorResponse.class);
        ResponseEntity<ErrorResponse> failCreatedUnknownChannel = rest.postForEntity("/api/messages", badRequestUnknownChannel, ErrorResponse.class);

        // then - 404 응답 + Exception 종류 확인
        assertThat(failCreatedUnknownUser.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failCreatedUnknownUser.getBody()).isNotNull();
        assertThat(failCreatedUnknownUser.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());

        // and then - 404 응답 + Exception 종류 확인
        assertThat(failCreatedUnknownChannel.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failCreatedUnknownChannel.getBody()).isNotNull();
        assertThat(failCreatedUnknownChannel.getBody().getCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND.name());

        // 알 수 없는 채널로 메시지 조회
        // and when - GET /api/messages?channelId={channel-id}
        ResponseEntity<ErrorResponse> failFindAllMessages = rest.getForEntity("/api/messages?channelId=" + UUID.randomUUID(), ErrorResponse.class);

        // and then - 404 응답 + Exception 종류 확인
        assertThat(failFindAllMessages.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failFindAllMessages.getBody()).isNotNull();
        assertThat(failFindAllMessages.getBody().getCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND.name());
    }

    @DisplayName("PathVariable로 받은 Id에 해당하는 메시지가 존재하지 않으면 실패한다.")
    @Test
    void shouldFailedWhenMessageNotFound() {
        // update의 경우
        // and given 수정 정보
        MessageUpdateRequest messageUpdateRequest = new MessageUpdateRequest("newContent");
        HttpEntity<MessageUpdateRequest> updateRequest = new HttpEntity<>(messageUpdateRequest);

        // when - PATCH /api/messages/{message-id}
        ResponseEntity<ErrorResponse> failUpdated = rest.exchange("/api/messages/" + UUID.randomUUID(), HttpMethod.PATCH, updateRequest, ErrorResponse.class);

        // then - 404 응답 + Exception 종류 확인
        assertThat(failUpdated.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failUpdated.getBody()).isNotNull();
        assertThat(failUpdated.getBody().getCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND.name());

        // and when - DELETE /api/messages/{message-id}
        ResponseEntity<ErrorResponse> failDeleted = rest.exchange("/api/messages/" + UUID.randomUUID(), HttpMethod.DELETE, updateRequest, ErrorResponse.class);

        // and then - 404 응답 + Exception 종류 확인
        assertThat(failDeleted.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failDeleted.getBody()).isNotNull();
        assertThat(failDeleted.getBody().getCode()).isEqualTo(ErrorCode.MESSAGE_NOT_FOUND.name());
    }

}
