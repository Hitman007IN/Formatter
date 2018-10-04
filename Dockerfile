FROM java:8
EXPOSE 8099
ADD /target/MyFormatter-0.0.1-SNAPSHOT.jar MyFormatter.jar
ENTRYPOINT ["java", "-jar", "MyFormatter.jar"]
