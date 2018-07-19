package demo.kafkabackend;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;
import static org.springframework.kafka.test.assertj.KafkaConditions.partition;
import static org.springframework.kafka.test.assertj.KafkaConditions.value;

@RunWith(SpringRunner.class)
@SpringBootTest
@Ignore
public class OpentracingDemoApplicationTests {

    static {
        System.setProperty("JAEGER_SERVICE_NAME", "backend-kafka");
    }

    private static final String TOPIC = "opentracing-demo";

    @ClassRule
    public static KafkaEmbedded embeddedKafka = new KafkaEmbedded(1, true, TOPIC);

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Test
    public void testName() throws Exception {
        // Container
        ContainerProperties containerProperties = new ContainerProperties(TOPIC);
        KafkaMessageListenerContainer<String, String> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        final BlockingQueue<ConsumerRecord<String, String>> records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, String>) record -> {
            System.out.println(record);
            records.add(record);
        });
        container.setBeanName("templateTests");
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());

        // Producer
        Map<String, Object> senderProps = KafkaTestUtils.senderProps(embeddedKafka.getBrokersAsString());
        ProducerFactory<String, String> pf = new DefaultKafkaProducerFactory<>(senderProps);
        KafkaTemplate<String, String> template = new KafkaTemplate<>(pf);
        template.setDefaultTopic(TOPIC);

        // Send
        template.sendDefault("foo");

        // Verify
        assertThat(records.poll(10, TimeUnit.SECONDS)).has(value("foo"));
        template.sendDefault(0, "2", "bar");
        ConsumerRecord<String, String> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).has(key("2"));
        assertThat(received).has(partition(0));
        assertThat(received).has(value("bar"));
        template.send(TOPIC, 0, "2", "baz");
        received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).has(key("2"));
        assertThat(received).has(partition(0));
        assertThat(received).has(value("baz"));
    }

}
