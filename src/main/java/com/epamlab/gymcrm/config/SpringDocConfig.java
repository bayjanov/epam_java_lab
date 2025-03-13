package com.epamlab.gymcrm.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM API")
                        .description("Gym CRM endpoints for Trainee/Trainer/Trainings")
                        .version("1.0.0"));
    }
}

