# 1. 빌드용 이미지 (eclipse-temurin - 윈도우, 리눅스, 맥OS 모두 호환)
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Gradle 관련 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 먼저 다운로드 (이 단계가 캐시됨)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 후 빌드
COPY src ./src
RUN ./gradlew build -x test --no-daemon

# 2. 실행용 이미지 (JRE로 경량화)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 빌드된 JAR 파일만 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]