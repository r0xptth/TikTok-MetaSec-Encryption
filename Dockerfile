FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /opt/metasec
COPY --from=build /src/target/tiktok-metasec-encryption.jar /opt/metasec/app.jar
RUN apt-get update \
 && apt-get install -y --no-install-recommends unzip \
 && rm -rf /var/lib/apt/lists/* \
 && unzip -j -o /opt/metasec/app.jar "natives/linux_64/libunicorn_java.so" -d /usr/lib || true \
 && if [ -f /usr/lib/libunicorn_java.so ]; then ln -sf /usr/lib/libunicorn_java.so /usr/lib/libunicorn.so; ldconfig; fi
EXPOSE 5099
CMD ["java", "-Dmetasec.bind=0.0.0.0", "-jar", "/opt/metasec/app.jar", "5099"]
