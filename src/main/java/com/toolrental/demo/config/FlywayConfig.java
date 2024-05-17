package com.toolrental.demo.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public CommandLineRunner flywayRunner(Flyway flyway) {
        return args -> {
            // Clean the existing schema
            flyway.clean();

            // Migrate to recreate the schema
            flyway.migrate();
        };
    }

}
