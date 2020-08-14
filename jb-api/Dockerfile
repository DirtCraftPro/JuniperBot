FROM openjdk:11.0.6-jre-stretch
RUN mkdir -p /JuniperBot/logs /JuniperBot/temp
WORKDIR /JuniperBot
COPY build/libs/JuniperBot-API.jar JuniperBot-API.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar JuniperBot-API.jar