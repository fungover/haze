FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ARG USERNAME=hazeuser
ARG USER_UID=1000
ARG USER_GID=$USER_UID
RUN groupadd --gid $USER_GID $USERNAME && \
    useradd --uid $USER_UID --gid $USER_GID -m $USERNAME
ADD https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-core/2.20.0/log4j-core-2.20.0.jar ./lib/
ADD https://repo1.maven.org/maven2/org/apache/logging/log4j/log4j-api/2.20.0/log4j-api-2.20.0.jar ./lib/
COPY target/classes .
RUN chown --recursive $USERNAME:$USERNAME .
USER $USERNAME
EXPOSE 6379
ENTRYPOINT ["java","-cp","/app:/app/lib/*","org.fungover.haze.Main"]
