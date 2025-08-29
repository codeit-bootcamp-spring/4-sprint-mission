FROM amazoncorretto:17

WORKDIR /app

COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle
COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew --no-daemon --refresh-dependencies dependencies || true
RUN ./gradlew build -x test

EXPOSE 80

ENV JVM_OPTS=""
ENV PROJECT_NAME=discodeit
ENV PROJECT_VERSION=1.2-M8

CMD ["java", "-jar", "build/libs/discodeit-0.0.1-SNAPSHOT.jar"]




