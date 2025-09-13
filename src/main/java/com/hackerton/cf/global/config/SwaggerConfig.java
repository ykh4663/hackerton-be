package com.hackerton.cf.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(
                title       = "Career Fit API",
                version     = "1.0.0",
                description = "API Description"
        )
//        servers = {
//                @Server(
//                        url         = "도메인 주소",
//                        description = "Production"
//                )
//        }
)
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtScheme = "JWT";
        SecurityRequirement securityReq = new SecurityRequirement().addList(jwtScheme);
        Components components = new Components()
                .addSecuritySchemes(jwtScheme, new SecurityScheme()
                        .name(jwtScheme)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                );

        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityReq)
                // 모델용 Info 를 이용해 문서 정보 설정
                .info(new Info()
                        .title("Career Fit API")
                        .description("API Description")
                        .version("1.0.0")
                );
    }
}