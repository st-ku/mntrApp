FROM openjdk:17-slim
WORKDIR /
COPY data data
ADD mntrApp-1.0-SNAPSHOT-all.jar mntrApp-1.0-SNAPSHOT-all.jar
CMD ["java", "-jar", "mntrApp-1.0-SNAPSHOT-all.jar"]