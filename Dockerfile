# ---------- build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .

# 1) Build + INSTALL para que los módulos queden en ~/.m2 (no solo package)
RUN mvn -q -DskipTests install

RUN mvn -q -pl notifications-examples -DskipTests dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=notifications-examples/target/deps

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/notifications-examples/target/notifications-examples-*.jar /app/app.jar

COPY --from=build /workspace/notifications-examples/target/deps /app/deps

CMD ["java", "-cp", "/app/app.jar:/app/deps/*", "com.agora.notifications.examples.DemoMain"]
