package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.DiscodeitProperties;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DiscodeitProperties.class)
public class DiscodeitApplication {

    // 테스트를 반복하면 데이터가 중복되는 현상때문에 파일을 다 지움
    public static void clearDataFiles() {
        File dataDir = new File("data");

        if (dataDir.exists() && dataDir.isDirectory()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        System.out.println("삭제됨: " + file.getName());
                    } else {
                        System.out.println("삭제 실패: " + file.getName());
                    }
                }
            }
            if (!dataDir.delete()) {
                System.out.println("data 디렉터리 삭제 실패");
            } else {
                System.out.println("data 디렉터리 삭제됨");
            }
        }

        if (!dataDir.exists()) {
            if (dataDir.mkdir()) {
                System.out.println("data 디렉터리 생성됨");
            } else {
                System.out.println("data 디렉터리 생성 실패");
            }
        }
    }

    public static void main(String[] args) {
        clearDataFiles();
        SpringApplication.run(DiscodeitApplication.class, args);
    }
}
