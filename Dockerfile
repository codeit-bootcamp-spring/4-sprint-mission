# 빌드 단계
FROM amazoncorretto:17 AS builder
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY settings.gradle settings.gradle
COPY build.gradle build.gradle

# 의존성 설치
RUN ./gradlew --no-daemon build -x test || true \
 && ./gradlew --no-daemon dependencies || true

# 소스 복사
COPY src ./src

# Gradle Wrapper로 빌드(bootJar)
RUN ./gradlew --no-daemon clean bootJar -x test

# 런타임 환경변수
ARG PROJECT_NAME
ARG PROJECT_VERSION
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JAVA_OPTS=""

#실행 단계
FROM amazoncorretto:17
WORKDIR /app

ARG PROJECT_NAME
ARG PROJECT_VERSION
ENV PROJECT_NAME=${PROJECT_NAME}
ENV PROJECT_VERSION=${PROJECT_VERSION}
ENV JAVA_OPTS=""

# 빌드 산출물 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]