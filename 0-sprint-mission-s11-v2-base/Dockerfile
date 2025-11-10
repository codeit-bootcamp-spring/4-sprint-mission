# 빌드 스테이지
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle 관련 파일 먼저 복사해서 캐시 활용
COPY gradle ./gradle
COPY gradlew ./gradlew
COPY build.gradle settings.gradle ./

# 의존성만 먼저 받아서 캐시
RUN ./gradlew dependencies

# 실제 소스 복사 후 빌드
COPY src ./src
# spring-boot면 bootJar가 더 명확하지만, 네 원래대로 build 해도 됨
RUN ./gradlew build -x test
# 또는
# RUN ./gradlew bootJar -x test


# 런타임 스테이지
FROM amazoncorretto:17-alpine3.21

WORKDIR /app

# 필요하면 JVM 옵션만 남겨두자
ENV JVM_OPTS=""

# 빌드 결과 jar 한 개만 가져와서 app.jar 라고 이름 붙이기
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 스프링 기본 포트
EXPOSE 8080

# 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar /app/app.jar"]
