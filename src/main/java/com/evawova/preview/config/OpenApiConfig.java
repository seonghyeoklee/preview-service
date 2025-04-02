package com.evawova.preview.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "JWT Auth";
        return new OpenAPI()
                .info(new Info()
                        .title("Preview Service API")
                        .version("1.0.0")
                        .description("Preview Service API Documentation")
                        .contact(new Contact()
                                .name("EvaWova")
                                .email("support@evawova.com")
                                .url("https://evawova.com"))
                        .license(new License()
                                .name("EvaWova License")
                                .url("https://evawova.com/license")))
                .addSecurityItem(new SecurityRequirement().addList(jwtSchemeName))
                .components(new Components()
                        .addSecuritySchemes(jwtSchemeName,
                                new SecurityScheme()
                                        .name(jwtSchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .addServersItem(new Server().url("http://localhost:8080").description("Local Development Server"))
                .addServersItem(new Server().url("https://api.evawova.com").description("Production Server"));
    }
} 