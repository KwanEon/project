# 1. Java 21 베이스 이미지 사용(eclipse Temurin = 윈도우, 리눅스, macOS에서 사용 가능)
FROM eclipse-temurin:21-jdk AS build

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. Gradle wrapper 복사 및 권한 부여
COPY gradlew .
COPY gradle gradle
RUN chmod +x gradlew

# 4. 프로젝트 복사 및 빌드
COPY . .
RUN ./gradlew build -x test --no-daemon

# 5. 실제 실행 환경용 이미지 생성
FROM eclipse-temurin:21-jdk
WORKDIR /app

# 6. 위 빌드 이미지에서 JAR만 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
