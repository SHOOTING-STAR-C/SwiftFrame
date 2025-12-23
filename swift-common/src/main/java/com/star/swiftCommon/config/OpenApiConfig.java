package com.star.swiftCommon.config;

import com.star.swiftCommon.constant.ResultCode;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * SpringDoc OpenAPI 3 配置类
 *
 * @author star
 */
@Slf4j
@Configuration
public class OpenApiConfig implements ApplicationListener<ApplicationReadyEvent> {

    @Bean
    public OpenAPI swiftFrameOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SwiftFrame API 接口文档")
                        .description(buildDescription())
                        .version("v1.0")
                        .contact(new Contact().name("star").email("star@example.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)));
    }

    private String buildDescription() {
        StringBuilder description = new StringBuilder();
        description.append("基于 Spring Boot 3 + SpringDoc 的后端架构接口文档<br/><br/>");
        description.append("### 响应状态码说明\n");
        description.append("| 状态码 | 描述 |\n");
        description.append("| :--- | :--- |\n");
        for (ResultCode resultCode : ResultCode.values()) {
            description.append("| ").append(resultCode.getCode()).append(" | ").append(resultCode.getMessage()).append(" |\n");
        }
        return description.toString();
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            String port = env.getProperty("server.port");
            if (!StringUtils.hasText(port)) {
                port = "8080";
            }
            String path = env.getProperty("server.servlet.context-path");
            if (!StringUtils.hasText(path)) {
                path = "";
            }
            log.info("\n----------------------------------------------------------\n\t" +
                    "Application is running! Access URLs:\n\t" +
                    "Local: \t\thttp://localhost:" + port + path + "/\n\t" +
                    "External: \thttp://" + ip + ":" + port + path + "/\n\t" +
                    "Swagger文档: \thttp://" + ip + ":" + port + path + "/swagger-ui/index.html\n" +
                    "----------------------------------------------------------");
        } catch (UnknownHostException e) {
            log.error("获取IP地址失败", e);
        }
    }
}
