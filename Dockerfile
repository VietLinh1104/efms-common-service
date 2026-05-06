# ============================================================
# Stage 1: Build
# ============================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom.xml trước để cache layer dependency
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (sẽ được cache nếu pom.xml không thay đổi)
RUN ./mvnw dependency:go-offline -B

# Copy source code và build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# ============================================================
# Stage 2: Runtime
# ============================================================
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# Tạo user non-root để bảo mật
RUN addgroup -S efms && adduser -S efms -G efms

# Copy JAR từ stage build
COPY --from=builder /app/target/*.jar app.jar

# Đổi owner về user non-root
RUN chown efms:efms app.jar
USER efms

# Expose port mặc định
EXPOSE 8080

# Thiết lập Spring profile production
ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "app.jar"]
