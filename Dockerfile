# ---------- build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .

# Build + install del reactor (módulos internos a ~/.m2)
RUN mvn -q -DskipTests install

# Asegura directorio (Maven no siempre lo crea)
RUN mkdir -p /workspace/notifications-examples/target/deps

# Copia deps runtime del módulo examples
RUN mvn -q -pl notifications-examples -DskipTests dependency:copy-dependencies \
  -DincludeScope=runtime \
  -DoutputDirectory=/workspace/notifications-examples/target/deps

# Verificación dura: si no existe o está vacío, falla aquí (no en el COPY)
RUN test -d /workspace/notifications-examples/target/deps && \
    ls -la /workspace/notifications-examples/target/deps

# ---------- runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/notifications-examples/target/notifications-examples-*.jar /app/app.jar
COPY --from=build /workspace/notifications-examples/target/deps /app/deps

CMD ["java", "-cp", "/app/app.jar:/app/deps/*", "com.agora.notifications.examples.DemoMain"]
