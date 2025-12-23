# 使用 Maven 镜像进行构建
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

# 复制 pom.xml 文件以利用 Docker 缓存
COPY pom.xml .
COPY swift-common/pom.xml swift-common/
COPY swift-datasource/pom.xml swift-datasource/
COPY swift-encrypt/pom.xml swift-encrypt/
COPY swift-encrypt-plugin/pom.xml swift-encrypt-plugin/
COPY swift-login/pom.xml swift-login/
COPY swift-redis/pom.xml swift-redis/
COPY swift-security/pom.xml swift-security/
COPY swift-start/pom.xml swift-start/

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码并进行构建
COPY . .
RUN mvn clean package -DskipTests

# 使用 JRE 镜像进行运行
FROM openjdk:17-jdk-slim
WORKDIR /app

# 从构建阶段复制打包好的 jar 文件
# 假设最终启动模块是 swift-start，生成的文件名可能需要根据实际 pom.xml 配置调整
COPY --from=build /app/swift-start/target/*.jar app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
