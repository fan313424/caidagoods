# 第一阶段：构建
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY caida/pom.xml .
COPY caida/src ./src
RUN mvn clean package -DskipTests -B

# 第二阶段：运行
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
