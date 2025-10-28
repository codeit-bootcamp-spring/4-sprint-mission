package com.sprint.mission.discodeit.async;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "async")
public class AsyncProperties {

  private Pool binaryContent = new Pool();
  private Pool notification = new Pool();

  @Getter
  @Setter
  public static class Pool {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
  }
}
