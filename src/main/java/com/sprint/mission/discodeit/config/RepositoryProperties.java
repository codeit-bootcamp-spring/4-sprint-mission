package com.sprint.mission.discodeit.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@AllArgsConstructor
@ConfigurationProperties(prefix = "discodeit.repository")
@Getter
public class RepositoryProperties {

    private final RepositoryType type;

    private final String fileDirectory;
}
