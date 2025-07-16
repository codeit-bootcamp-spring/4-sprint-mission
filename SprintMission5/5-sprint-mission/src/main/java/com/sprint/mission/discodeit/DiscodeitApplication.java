package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.config.DiscodeitProperties;
import java.io.File;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DiscodeitProperties.class)
public class DiscodeitApplication {

  // 파일 및 서브디렉토리를 재귀적으로 삭제하는 메서드
  private static void deleteDirectoryContents(File directory) {
    if (directory.exists() && directory.isDirectory()) {
      File[] files = directory.listFiles();
      if (files != null) {
        for (File file : files) {
          if (file.isDirectory()) {
            deleteDirectoryContents(file);
          }
          if (file.delete()) {
            System.out.println("삭제됨: " + file.getAbsolutePath());
          } else {
            System.err.println("삭제 실패: " + file.getAbsolutePath());
          }
        }
      }
    }
  }

  public static void clearDataFiles() {
    File dataDir = new File(".discodeit"); // 최상위 "data" 디렉토리를 대상으로 합니다.

    if (dataDir.exists() && dataDir.isDirectory()) {
      System.out.println("기존 '" + dataDir.getPath() + "' 디렉터리 내용을 삭제합니다...");
      deleteDirectoryContents(dataDir); // 모든 내용을 재귀적으로 삭제

      if (dataDir.delete()) { // 이제 비어있는 "data" 디렉토리 자체를 삭제
        System.out.println("'" + dataDir.getPath() + "' 디렉터리 삭제됨");
      } else {
        System.err.println("'" + dataDir.getPath() + "' 디렉터리 삭제 실패");
      }
    }

    if (!dataDir.exists()) {
      if (dataDir.mkdir()) {
        System.out.println("'" + dataDir.getPath() + "' 디렉터리 생성됨");
      } else {
        System.err.println("'" + dataDir.getPath() + "' 디렉터리 생성 실패");
      }
    }
  }

  public static void main(String[] args) {
    clearDataFiles();
    SpringApplication.run(DiscodeitApplication.class, args);
  }
}