package demo.kafkabackend.configuration;

import io.opentracing.Tracer;
import io.opentracing.contrib.kafka.spring.TracingConsumerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfiguration {
    private final KafkaProperties kafkaProperties;
    private final Tracer tracer;

    @Bean
    public ConsumerFactory<?, ?> consumerFactory() {
        Map<String, Object> consumerProperties = kafkaProperties.getConsumer().buildProperties();
        return new TracingConsumerFactory<>(new DefaultKafkaConsumerFactory<>(consumerProperties), tracer);
    }
}
