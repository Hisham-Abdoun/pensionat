package org.example.pensionat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Detta är för att se våran API dokumentation på http://localhost:8080/swagger-ui/
// eller http://localhost:8080/v3/api-docs
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Booking Service API")
                        .description("REST API för bokningssystem")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Booking Service")));
    }
}
