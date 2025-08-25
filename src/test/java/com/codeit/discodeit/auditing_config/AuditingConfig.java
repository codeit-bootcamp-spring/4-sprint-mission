package com.codeit.discodeit.auditing_config;

import java.util.Optional;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@TestConfiguration
@EnableJpaAuditing(auditorAwareRef = "testAuditorProvider")
public class AuditingConfig {

  @Bean
  public AuditorAware<String> testAuditorProvider() {
    return () -> Optional.of("testUser");
  }

}
