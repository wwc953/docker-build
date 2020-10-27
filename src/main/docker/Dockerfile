FROM lwieske/java-8:latest
VOLUME /tmp
ADD docker-build-1.1.jar app.jar
EXPOSE 8000
ENTRYPOINT [ "java", "-jar", "/app.jar" ]