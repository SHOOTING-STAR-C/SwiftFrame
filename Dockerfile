# 使用 Maven 镜像进行构建
FROM maven:3.9.9-eclipse-temurin-21-jammy AS build
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
COPY swift-config/pom.xml swift-config/
COPY swift-monitor/pom.xml swift-monitor/
COPY swift-ai/pom.xml swift-ai/
COPY swift-business/pom.xml swift-business/
COPY swift-mail/pom.xml swift-mail/
COPY swift-start/pom.xml swift-start/

COPY swift-encrypt-plugin/src swift-encrypt-plugin/src
COPY swift-encrypt/src swift-encrypt/src
RUN mvn clean install -pl swift-encrypt-plugin -am -DskipTests

# 下载依赖
RUN mvn dependency:go-offline -B

# 复制源代码并进行构建
COPY . .
# 设置构建环境变量，使用 prod 配置
ARG APP_ENV=prod
ENV APP_ENV=${APP_ENV}
RUN mvn clean package -DskipTests -Dapp.env=${APP_ENV}

# 使用 JRE 镜像进行运行
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# 创建 mime.types 文件以避免 FileNotFoundException 警告
RUN mkdir -p /root && touch /root/.mime.types

# 从构建阶段复制打包好的可执行 jar 文件
COPY --from=build /app/swift-start/target/swift-start-*-exec.jar app.jar

# 设置环境变量
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=prod

# 暴露端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
