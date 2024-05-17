package com.toolrental.demo.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The <code>FlywayConfig</code> class is used to configure Flyway to clean and migrate the schema.
 */
@Configuration
public class FlywayConfig {

    /**
     * flywayRunner method is used to clean and migrate the schema.
     * @param flyway A flyway instance.
     * @return A <code>CommandLineRunner</code> instance.
     */
    @Bean
    public CommandLineRunner flywayRunner(Flyway flyway) {
        return args -> {
            flyway.clean(); // Clean the existing schema
            flyway.migrate(); // Migrate to recreate the schema
        };
    }

}
