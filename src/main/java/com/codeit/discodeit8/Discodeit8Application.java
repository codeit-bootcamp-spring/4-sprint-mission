package com.codeit.discodeit8;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
//@EnableJpaAuditing // 슬라이스 테스트 코드 실행 시에 문제가 발생함, 통합 테스트 때에는 주석 풀기, 그 때엔 주석 처리 필요함
public class Discodeit8Application {

  public static void main(String[] args) {
    SpringApplication.run(Discodeit8Application.class, args);
  }

}
