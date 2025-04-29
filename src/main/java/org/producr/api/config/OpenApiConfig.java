package org.producr.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan({"org.producr"})
@EnableTransactionManagement
@EnableConfigurationProperties
public class OpenApiConfig {


  @Bean
  GroupedOpenApi apiV1() {
    return GroupedOpenApi.builder().group("v1").pathsToMatch("/v1/**").build();
  }

  @Bean
  OpenAPI rideShareUserApiV1() {
    return new OpenAPI().info(new Info().title("Sky Weave API")
        .description("Api docs for Sky Weave application").version("0.0.1"));
  }

  @Bean
  @Primary
  public OpenAPI customOpenAPI() {
    return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("BearerAuth")) // Enables
        .components(new Components().addSecuritySchemes("BearerAuth",
            new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer")
                .bearerFormat("JWT")))
        .info(new Info().title("PRODUCR Api ").description("Api docs for PRODUCR application")
            .version("0.0.1")); // You can change this to Basic if using Basic Auth
  }
}
