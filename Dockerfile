# =============================
# 1) 빌드 단계
# =============================
FROM amazoncorretto:17 AS builder

WORKDIR /app

# Gradle Wrapper 및 의존성 관련 파일만 먼저 복사 (캐시 최적화)
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./
RUN chmod +x ./gradlew

# 의존성만 먼저 다운로드 (소스 코드 변경시에도 캐시 활용 가능)
RUN ./gradlew --no-daemon dependencies || true

# 이후 전체 프로젝트 복사
COPY . .

# 불필요한 task 제외하고 bootJar만 빌드 (속도 최적화)
RUN ./gradlew clean bootJar --no-daemon -x test

# =============================
# 2) 실행 단계 (경량 JRE 사용)
# =============================
FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

# JVM 최적화 옵션 (컨테이너 환경에 맞춤)
ENV JAVA_TOOL_OPTIONS="-XX:+ExitOnOutOfMemoryError -XX:MaxRAMPercentage=75.0"

# 빌드 결과물 복사 (버전 바뀌어도 자동 매칭되도록 *)
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 80

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
