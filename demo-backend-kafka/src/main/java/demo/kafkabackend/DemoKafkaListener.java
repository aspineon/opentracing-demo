package demo.kafkabackend;

import io.opentracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;

@Component
@RequiredArgsConstructor
@Slf4j
public class DemoKafkaListener {

    public static final String TOPIC = "opentracing-demo";
    public static final String GROUPID = "kafka-backend";

    @Value("http://${backend.rest.hostport}/api/get")
    private String restEndPoint;

    private final RestOperations restTemplate;
    private final Tracer tracer;

    @KafkaListener(topics = TOPIC)
    public void listen(
            @Header("uber-trace-id") byte[] uberTrace,
            @Header("second_span_uber-trace-id") byte[] secondTrace,
//            @Headers Map<String, Object> headers,
            @Payload String message) {
//        headers.forEach((k, v) -> log.info("Header '{}': '{}'", k, v))

        log.info("Received " + message);
        log.info("Uber trace id:   {}", new String(uberTrace));
        log.info("Second trace id: {}", new String(secondTrace));
        log.info("Active span: {}", tracer.activeSpan());

        // FIXME propagation stops here as active span is null; a new trace is started
        ResponseEntity response = restTemplate.getForEntity(restEndPoint, Void.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("REST API call returns OK");
        } else {
            log.info("REST API call does not return OK :(");
        }
    }
}
