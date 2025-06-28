package com.sprint.mission.discodeit.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class FileDirectoryConfig {

    private final RepositoryProperties repositoryProperties;

    @Bean
    public String getFileDirectory() {
        return repositoryProperties.getFileDirectory();
    }
}
