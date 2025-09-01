package com.sprint.mission.discodeit.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.JpaAuditingTestConfig;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
public class UserE2ETest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatusRepository userStatusRepository;

    @Autowired
    private BinaryContentRepository binaryContentRepository;

    private static final String BASE_URL = "/api/users";

    @BeforeEach
    void setUp() {
        // 각 테스트 전에 데이터 초기화
        userStatusRepository.deleteAll();
        userRepository.deleteAll();
        binaryContentRepository.deleteAll();
    }

    @Test
    @DisplayName("사용자 생성 - 성공 (프로필 이미지 포함)")
    void createUser_Success_WithProfile() throws Exception {
        // Given
        UserCreateRequest request = new UserCreateRequest(
                "testuser",
                "test@example.com",
                "password123"
        );

        byte[] profileBytes = "test profile image".getBytes();
        ByteArrayResource profileResource = new ByteArrayResource(profileBytes) {
            @Override
            public String getFilename() {
                return "profile.jpg";
            }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("userCreateRequest", request);
        body.add("profile", profileResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // When
        ResponseEntity<UserDto> response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                UserDto.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().username()).isEqualTo("testuser");
        assertThat(response.getBody().email()).isEqualTo("test@example.com");
        assertThat(response.getBody().profile()).isNotNull();
        assertThat(response.getBody().online()).isNotNull();

        // DB 검증
        assertThat(userRepository.count()).isEqualTo(1);
        User savedUser = userRepository.findByUsername("testuser").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }
}
