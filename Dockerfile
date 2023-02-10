FROM eclipse-temurin:19-jre-jammy
COPY target/classes /app
WORKDIR /app
EXPOSE 6379
ENTRYPOINT ["java","-cp" , "/app", "--enable-preview", "org.fungover.haze.Main"]
