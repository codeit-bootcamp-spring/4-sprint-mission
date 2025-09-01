package com.codeit.discodeit8.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 각 테스트가 독립적으로 실행되도록
@ActiveProfiles("test") // application-test.yml 사용
@AutoConfigureMockMvc
public class IntegrationTestBasic {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;
}
