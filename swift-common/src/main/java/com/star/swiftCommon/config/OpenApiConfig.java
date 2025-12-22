package com.star.swiftCommon.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc OpenAPI 3 配置类
 *
 * @author star
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI swiftFrameOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SwiftFrame API 接口文档")
                        .description("基于 Spring Boot 3 + SpringDoc 的后端架构接口文档")
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
}
