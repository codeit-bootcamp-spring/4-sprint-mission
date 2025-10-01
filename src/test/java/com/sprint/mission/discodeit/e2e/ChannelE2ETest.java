package com.sprint.mission.discodeit.e2e;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.repository.ChannelRepository;
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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class ChannelE2ETest {
    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ChannelRepository channelRepository;
    @Autowired
    private UserRepository userRepository;
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
        userRepository.deleteAll();
        channelRepository.deleteAll();
    }

    @DisplayName("공개 채널 생성")
    @Test
    void createPublic() {
        // given
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // when - POST /api/channel/public
        ResponseEntity<ChannelDto> response = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("description");
        assertThat(response.getBody().name()).isEqualTo("channelName");
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = response.getBody().id();
        assertThat(channelId).isNotNull();
    }

    @DisplayName("비공개 채널 생성")
    @Test
    void createPrivate() {
        // given - 비공개 채널에 참가할 유저 생성 정보
        UserCreateRequest hongCreateRequest = new UserCreateRequest("hong", "hong1234", "hong@example.com");
        UserCreateRequest leeCreateRequest = new UserCreateRequest("lee", "lee1234", "lee@example.com");
        MultiValueMap<String, Object> hongRequest = new LinkedMultiValueMap<>();
        hongRequest.add("userCreateRequest", hongCreateRequest);
        hongRequest.add("profile", null);
        MultiValueMap<String, Object> leeRequest = new LinkedMultiValueMap<>();
        leeRequest.add("userCreateRequest", leeCreateRequest);
        leeRequest.add("profile", null);

        // given & when - POST /api/users
        UserDto hong = rest.postForObject("/api/users", hongRequest, UserDto.class);
        UserDto lee = rest.postForObject("/api/users", leeRequest, UserDto.class);

        // and given - 비공개 채널에 참가할 유저
        List<UUID> participants = Arrays.asList(hong.id(), lee.id());
        PrivateChannelCreateRequest channelCreateRequest = new PrivateChannelCreateRequest(participants);

        // and when - POST /api/channels/private
        ResponseEntity<ChannelDto> response = rest.postForEntity("/api/channels/private", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 이름과 설명이 없음 확인 + 채널 타입 Private 확인 + id 추가 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isNullOrEmpty();
        assertThat(response.getBody().name()).isNullOrEmpty();
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PRIVATE);
        UUID channelId = response.getBody().id();
        assertThat(channelId).isNotNull();
    }

    @DisplayName("채널 수정")
    @Test
    void update() {
        // given
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // when - POST /api/channel/public
        ResponseEntity<ChannelDto> response = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("description");
        assertThat(response.getBody().name()).isEqualTo("channelName");
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID channelId = response.getBody().id();
        assertThat(channelId).isNotNull();

        // and given - Update 정보
        PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest("newChannelName", "newDescription");
        HttpEntity<PublicChannelUpdateRequest> request = new HttpEntity<>(channelUpdateRequest);

        // when - PATCH /api/channels/{channel-id}
        ResponseEntity<ChannelDto> updated = rest.exchange("/api/channels/" + channelId, HttpMethod.PATCH, request, ChannelDto.class);

        // then - 200 응답 + 내용 확인 + 타입 public 확인
        assertThat(updated.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updated.getBody()).isNotNull();
        assertThat(updated.getBody().description()).isEqualTo("newDescription");
        assertThat(updated.getBody().name()).isEqualTo("newChannelName");
        assertThat(updated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
    }

    @DisplayName("채널 삭제")
    @Test
    void delete() {
        // given
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // when - POST /api/channel/public
        ResponseEntity<ChannelDto> publicCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(publicCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(publicCreated.getBody()).isNotNull();
        assertThat(publicCreated.getBody().description()).isEqualTo("description");
        assertThat(publicCreated.getBody().name()).isEqualTo("channelName");
        assertThat(publicCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID publicChannelId = publicCreated.getBody().id();
        assertThat(publicChannelId).isNotNull();

        // and given - 비공개 채널에 참가할 유저 생성 정보
        UserCreateRequest hongCreateRequest = new UserCreateRequest("hong", "hong1234", "hong@example.com");
        UserCreateRequest leeCreateRequest = new UserCreateRequest("lee", "lee1234", "lee@example.com");
        MultiValueMap<String, Object> hongRequest = new LinkedMultiValueMap<>();
        hongRequest.add("userCreateRequest", hongCreateRequest);
        hongRequest.add("profile", null);
        MultiValueMap<String, Object> leeRequest = new LinkedMultiValueMap<>();
        leeRequest.add("userCreateRequest", leeCreateRequest);
        leeRequest.add("profile", null);

        // and given & when - POST /api/users
        UserDto hong = rest.postForObject("/api/users", hongRequest, UserDto.class);
        UserDto lee = rest.postForObject("/api/users", leeRequest, UserDto.class);

        // and given - 비공개 채널에 참가할 유저
        List<UUID> participants = Arrays.asList(hong.id(), lee.id());
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(participants);

        // and when - POST /api/channels/private
        ResponseEntity<ChannelDto> privateCreated = rest.postForEntity("/api/channels/private", privateChannelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 이름과 설명이 없음 확인 + 채널 타입 Private 확인 + id 추가 확인
        assertThat(privateCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(privateCreated.getBody()).isNotNull();
        assertThat(privateCreated.getBody().description()).isNullOrEmpty();
        assertThat(privateCreated.getBody().name()).isNullOrEmpty();
        assertThat(privateCreated.getBody().type()).isEqualTo(ChannelType.PRIVATE);
        UUID privateChannelId = privateCreated.getBody().id();
        assertThat(privateChannelId).isNotNull();

        // and when - GET /api/channels?userId={user-id} 쿼리 파라미터로 유저 아이디를 받음
        ResponseEntity<List> findChannels = rest.getForEntity("/api/channels?userId=" + hong.id(), List.class);

        // and then - 200 응답 + 채널 개수 확인
        assertThat(findChannels.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findChannels.getBody()).isNotNull();
        assertThat(findChannels.getBody().size()).isEqualTo(2);

        // and when & then - DELETE /api/channels/{channel-id}
        // 203 응답 + 바디가 비어 있음 확인
        ResponseEntity deleted = rest.exchange("/api/channels/" + publicChannelId, HttpMethod.DELETE, HttpEntity.EMPTY, ChannelDto.class);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(deleted.getBody()).isNull();

        // and when & then - GET /api/channels?userId={user-id} 다시 한번 조회로 변화 확인
        // 200 응답 + 채널의 수가 하나 줄어듦 확인
        findChannels = rest.getForEntity("/api/channels?userId=" + hong.id(), List.class);
        assertThat(findChannels.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findChannels.getBody()).isNotNull();
        assertThat(findChannels.getBody().size()).isEqualTo(1);
    }

    @DisplayName("유저ID로 채널 조회")
    @Test
    void findByUserId() {
        // given
        PublicChannelCreateRequest channelCreateRequest = new PublicChannelCreateRequest("channelName", "description");

        // when - POST /api/channel/public
        ResponseEntity<ChannelDto> publicCreated = rest.postForEntity("/api/channels/public", channelCreateRequest, ChannelDto.class);

        // then - 201 + 채널 정보 확인 + type Public 확인 + id 추가 확인
        assertThat(publicCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(publicCreated.getBody()).isNotNull();
        assertThat(publicCreated.getBody().description()).isEqualTo("description");
        assertThat(publicCreated.getBody().name()).isEqualTo("channelName");
        assertThat(publicCreated.getBody().type()).isEqualTo(ChannelType.PUBLIC);
        UUID publicChannelId = publicCreated.getBody().id();
        assertThat(publicChannelId).isNotNull();

        // and given - 비공개 채널에 참가할 유저 생성 정보
        UserCreateRequest hongCreateRequest = new UserCreateRequest("hong", "hong1234", "hong@example.com");
        UserCreateRequest leeCreateRequest = new UserCreateRequest("lee", "lee1234", "lee@example.com");
        MultiValueMap<String, Object> hongRequest = new LinkedMultiValueMap<>();
        hongRequest.add("userCreateRequest", hongCreateRequest);
        hongRequest.add("profile", null);
        MultiValueMap<String, Object> leeRequest = new LinkedMultiValueMap<>();
        leeRequest.add("userCreateRequest", leeCreateRequest);
        leeRequest.add("profile", null);

        // and given & when - POST /api/users
        UserDto hong = rest.postForObject("/api/users", hongRequest, UserDto.class);
        UserDto lee = rest.postForObject("/api/users", leeRequest, UserDto.class);

        // and given - 비공개 채널에 참가할 유저
        List<UUID> participants = Arrays.asList(hong.id(), lee.id());
        PrivateChannelCreateRequest privateChannelCreateRequest = new PrivateChannelCreateRequest(participants);

        // and when - POST /api/channels/private
        ResponseEntity<ChannelDto> privateCreated = rest.postForEntity("/api/channels/private", privateChannelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 이름과 설명이 없음 확인 + 채널 타입 Private 확인 + id 추가 확인
        assertThat(privateCreated.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(privateCreated.getBody()).isNotNull();
        assertThat(privateCreated.getBody().description()).isNullOrEmpty();
        assertThat(privateCreated.getBody().name()).isNullOrEmpty();
        assertThat(privateCreated.getBody().type()).isEqualTo(ChannelType.PRIVATE);
        UUID privateChannelId = privateCreated.getBody().id();
        assertThat(privateChannelId).isNotNull();

        // and when - GET /api/channels?userId={user-id} 쿼리파라미터로 받음
        ResponseEntity<List> findChannels = rest.getForEntity("/api/channels?userId=" + hong.id(), List.class);

        // and then - 200 응답 + 채널의 수 확인
        assertThat(findChannels.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(findChannels.getBody()).isNotNull();
        assertThat(findChannels.getBody().size()).isEqualTo(2);
    }

    @DisplayName("채널 수정 실패 / 비공개 채널은 수정할 수 없음")
    @Test
    void updateShouldFailedWhenPrivateChannelUpdate() {
        // given - 비공개 채널에 참가할 유저 생성 정보
        UserCreateRequest hongCreateRequest = new UserCreateRequest("hong", "hong1234", "hong@example.com");
        UserCreateRequest leeCreateRequest = new UserCreateRequest("lee", "lee1234", "lee@example.com");
        MultiValueMap<String, Object> hongRequest = new LinkedMultiValueMap<>();
        hongRequest.add("userCreateRequest", hongCreateRequest);
        MultiValueMap<String, Object> leeRequest = new LinkedMultiValueMap<>();
        leeRequest.add("userCreateRequest", leeCreateRequest);

        // given & when - POST /api/users
        UserDto hong = rest.postForObject("/api/users", hongRequest, UserDto.class);
        UserDto lee = rest.postForObject("/api/users", leeRequest, UserDto.class);

        // and given - 비공개 채널에 참가할 유저
        List<UUID> participants = Arrays.asList(hong.id(), lee.id());
        PrivateChannelCreateRequest channelCreateRequest = new PrivateChannelCreateRequest(participants);

        // and when - POST /api/channels/private
        ResponseEntity<ChannelDto> response = rest.postForEntity("/api/channels/private", channelCreateRequest, ChannelDto.class);

        // and then - 201 + 채널 이름과 설명이 없음 확인 + 채널 타입 Private 확인 + id 추가 확인
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isNullOrEmpty();
        assertThat(response.getBody().name()).isNullOrEmpty();
        assertThat(response.getBody().type()).isEqualTo(ChannelType.PRIVATE);
        UUID channelId = response.getBody().id();
        assertThat(channelId).isNotNull();

        // and given - Update 정보
        PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest("newChannelName", "newDescription");
        HttpEntity<PublicChannelUpdateRequest> request = new HttpEntity<>(channelUpdateRequest);

        // and when - PATCH /api/channels/{channel-id}
        ResponseEntity<ErrorResponse> errorResponse = rest.exchange("/api/channels/" + channelId, HttpMethod.PATCH, request, ErrorResponse.class);

        // and then - 400 응답 + Exception 종류 확인
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(errorResponse.getBody()).isNotNull();
        assertThat(errorResponse.getBody()).isInstanceOf(ErrorResponse.class);
        assertThat(errorResponse.getBody().getCode()).isEqualTo(ErrorCode.PRIVATE_CHANNEL_CANNOT_UPDATE.name());
    }

    @DisplayName("PathVariable로 받은 Id에 해당하는 채널이 존재하지 않는 경우 실패한다.")
    @Test
    void shouldFailedWhenChannelNotFound() {
        // update의 경우
        // given - Update 정보
        PublicChannelUpdateRequest channelUpdateRequest = new PublicChannelUpdateRequest("newChannelName", "newDescription");
        HttpEntity<PublicChannelUpdateRequest> request = new HttpEntity<>(channelUpdateRequest);

        // when - PATCH /api/channels/{channel-id}
        ResponseEntity<ErrorResponse> failUpdated = rest.exchange("/api/channels/" + UUID.randomUUID(), HttpMethod.PATCH, request, ErrorResponse.class);

        // then - 404 응답 + Exception 종류 확인
        assertThat(failUpdated.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failUpdated.getBody()).isNotNull();
        assertThat(failUpdated.getBody().getCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND.name());

        // delete의 경우
        // and when - DELETE /api/channels/{channel-id}
        ResponseEntity<ErrorResponse> failDeleted = rest.exchange("/api/channels/" + UUID.randomUUID(), HttpMethod.DELETE, request, ErrorResponse.class);

        // and then - 404 응답 + Exception 종류 확인
        assertThat(failDeleted.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failDeleted.getBody()).isNotNull();
        assertThat(failDeleted.getBody().getCode()).isEqualTo(ErrorCode.CHANNEL_NOT_FOUND.name());
    }

    @DisplayName("UserId로 채널 조회 시 UserId에 해당하는 유저가 존재하지 않으면 실패한다.")
    @Test
    void shouldFailedWhenUserNotFound() {
        // when - GET /api/channels?userId={user-id} 쿼리파라미터로 받음
        ResponseEntity<ErrorResponse> failed = rest.getForEntity("/api/channels?userId=" + UUID.randomUUID(), ErrorResponse.class);

        // then - 404 응답 + Exception 종류 확인
        assertThat(failed.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(failed.getBody()).isNotNull();
        assertThat(failed.getBody().getCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }
}
