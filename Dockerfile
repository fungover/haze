FROM eclipse-temurin:20-jre-jammy
COPY target/dependency /lib
COPY target/classes /app
WORKDIR /app
EXPOSE 6379
ENTRYPOINT ["java","-cp" , "/app:/lib/*", "--enable-preview", "org.fungover.haze.Main"]
