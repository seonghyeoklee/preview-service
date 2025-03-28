package com.evawova.preview.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Preview Service API")
                        .description("Preview 서비스 API 문서")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Developer")
                                .email("dev@evawova.com")
                                .url("https://evawova.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0.html")))
                .addServersItem(new Server().url("/").description("Default Server"))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", 
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")));
    }
    
    @Bean
    public GroupedOpenApi v1UserApi() {
        return GroupedOpenApi.builder()
                .group("v1-user-api")
                .pathsToMatch("/api/v1/users/**")
                .displayName("User API v1")
                .build();
    }
    
    @Bean
    public GroupedOpenApi v1PlanApi() {
        return GroupedOpenApi.builder()
                .group("v1-plan-api")
                .pathsToMatch("/api/v1/plans/**")
                .displayName("Plan API v1")
                .build();
    }
    
    @Bean
    public GroupedOpenApi v1AuthApi() {
        return GroupedOpenApi.builder()
                .group("v1-auth-api")
                .pathsToMatch("/api/v1/auth/**")
                .displayName("Authentication API v1")
                .build();
    }
} 