# use maven build to package application code and get jar file
FROM maven:3.8.1-openjdk-16-slim AS MAVEN_BUILD
WORKDIR /java_tasks
COPY src src/
COPY pom.xml ./
RUN mvn clean package

# copy only jar file from previous build
FROM openjdk:16-slim
COPY --from=MAVEN_BUILD /java_tasks/target/java_tasks-1.0-SNAPSHOT-jar-with-dependencies.jar /java_tasks.jar
RUN echo "#!/bin/sh" > startIOT.sh
RUN echo "java -cp java_tasks.jar IOTSimulator &" >> startIOT.sh
RUN echo "java -cp java_tasks.jar IOTSimulator &" >> startIOT.sh
RUN echo "java -cp java_tasks.jar IOTSimulator &" >> startIOT.sh
RUN echo "tail -f /dev/null" >> startIOT.sh
RUN chmod 755 startIOT.sh
CMD ["/startIOT.sh"]
