package com.example.Uplink_Backend.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI uplinkOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Uplink - AI-Powered Academia-Industry Collaboration & Placement Platform")
                        .description("Comprehensive REST API for students, universities, recruiters, and admins. " +
                                "Includes Auth, Profiles, Assessments, Opportunities, Applications, AI Recommendations, " +
                                "Messaging, Notifications, OCR, Resume Generation, Content Feed, Verification, and Analytics.")
                        .version("1.0.0")
                        .contact(new Contact().name("Uplink Platform Team").email("support@uplink.edu"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}
