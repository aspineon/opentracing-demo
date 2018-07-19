package demo.frontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestOperations;

@RestController
@RequiredArgsConstructor
public class IndexController {
    @Value("http://${backend.rest.hostport}/api/get")
    private String restEndPoint;

    @Value("${kafka.topic}")
    private String kafkaTopic;

    private final RestOperations restTemplate;
    private final KafkaTemplate<String, String> kafka;

    @RequestMapping("/")
    public String index() {
        String responseBody = "";

        ResponseEntity response = restTemplate.getForEntity(restEndPoint, Void.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            responseBody += "REST API call returns OK<br />";
        } else {
            responseBody += "REST API call does not return OK :(<br />";
        }

        for (int i = 1; i < 6; i++) {
            String messageId = String.format("Message %d", i);
            responseBody += String.format("%s sent to Kafka<br />", messageId);
            kafka.send(kafkaTopic, "Message", messageId);
        }

        return responseBody;
    }
}
