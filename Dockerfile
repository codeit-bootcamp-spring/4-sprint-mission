#ARG는 값을 외부에서 덮어쓸 수 있으므로 사용x
# =========== (1) Build =============
FROM gradle:7.6.0-jdk17 AS build

# 루트 권한으로 변경 (권한 설정/ 폴더 생성작업을 위해)
USER root
# 애플리케이션 작업 디렉토리 설정
WORKDIR /app
# Gradle 캐시 디토리와 앱 디렉토리 소유자를 gradle 유저로 변경
RUN mkdir -p /home/gradle/.gradle && chown -R gradle:gradle /home/gradle /app
# gradle 유저로 변경(보안 및 권한 문제 방지)
User gradle

# Gradle Wrapper 스크립트 복사 (빌드 실행에 필요)
COPY --chown=gradle:gradle gradlew ./
# gradle 폴더 복사 (wrapper 설정 및 실행 환경)
COPY --chown=gradle:gradle gradle ./gradle
# Gradle 설정 파일 복사 (빌드 스크립트)
COPY --chown=gradle:gradle settings.gradle build.gradle ./
# gradlew 실행 권한 부여
RUN chmod +x ./gradlew
# 의존성만 먼저 다운로드하여 캐시 활용 (코드 변경 없이 재사용 가능)
RUN ./gradlew --no-daemon --refresh-dependencies dependencies || true
# 실제 소스코드 복사 (이 시점 이후 변경 시 빌드 다시 수행됨)
COPY --chown=gradle:gradle src ./src
# 애플리케이션 빌드
RUN ./gradlew clean build


# ============ (2) Runtime ============
# 런타임 스테이지: 빌드 결과 실행에 필요한 최소한의 경량 이미지 사용
FROM amazoncorretto:17
# 앱 실행 디렉토리 지정
WORKDIR /app

# 빌드 스테이지에서 생성한 JAR 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar
# 애플리케이션이 사용하는 포트 노출
EXPOSE 80
# 프로젝트 정보 환경변수 설정
ENV PROJECT_VERSION=1.2-M8
ENV PROJECT_NAME=discodeit
#JVM_OPTS: JVM 옵션
ENV JVM_OPTS=""
# Spring Boot 프로필을 운영(prod)으로 설정
ENV SPRING_PROFILES_ACTIVE=prod
# 컨테이너 시작 시 JAR 실행
#CMD["sh", "-c", "exec", "java", "${JVM_OPTS}", "-jar", "${PROJECT_NAME}-${PROJECT_VERSION}.jar"]
ENTRYPOINT ["java", "-jar", "app.jar"]
