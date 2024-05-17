FROM openjdk:17-jdk-slim
ENV APP_PROFILE=default
COPY . /app
WORKDIR /app
RUN ./gradlew clean build -x test
CMD ["./gradlew", "bootRun", "-DAPP_PROFILE=${APP_PROFILE}"]
