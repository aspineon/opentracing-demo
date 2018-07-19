package demo.restbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ApiController {
    @GetMapping("/api/get")
    public ResponseEntity get(@RequestHeader HttpHeaders headers) {
        log.info("Headers: {}", headers);
        return new ResponseEntity(HttpStatus.OK);
    }
}
