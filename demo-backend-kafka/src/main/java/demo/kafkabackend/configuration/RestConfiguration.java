package demo.kafkabackend.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;

@Configuration
public class RestConfiguration {
    @Bean
    public RestOperations tracingClient(RestTemplateBuilder builder) {
        return builder.build();
    }
}
