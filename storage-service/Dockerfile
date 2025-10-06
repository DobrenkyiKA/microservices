# Stage for resolving and downloading dependencies
FROM maven:3.9-eclipse-temurin-21-alpine AS deps

WORKDIR /build

# Copy only the pom.xml to leverage Docker layer caching for dependencies
COPY pom.xml .

# Download dependencies using Maven with a cache mount for the local repository
RUN --mount=type=cache,target=/root/.m2 mvn -B -ntp -DskipTests dependency:go-offline

################################################################################

# Build the application
FROM deps AS package

WORKDIR /build

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -DskipTests clean package spring-boot:repackage && \
    mv target/$(mvn -q -DforceStdout help:evaluate -Dexpression=project.artifactId)-$(mvn -q -DforceStdout help:evaluate -Dexpression=project.version).jar target/app.jar

################################################################################

# Runtime image
FROM eclipse-temurin:21-jre-alpine AS final

# Create a non-privileged user that the app will run under.
ARG UID=10001
RUN adduser \
    --disabled-password \
    --gecos "" \
    --home "/nonexistent" \
    --shell "/sbin/nologin" \
    --no-create-home \
    --uid "${UID}" \
    appuser
USER appuser

# Copy the executable from the "package" stage.
COPY --from=package /build/target/app.jar app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8094

CMD java -jar app.jar
