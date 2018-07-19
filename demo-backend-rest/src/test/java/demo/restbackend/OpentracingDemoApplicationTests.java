package demo.restbackend;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OpentracingDemoApplicationTests {
    static {
        System.setProperty("JAEGER_SERVICE_NAME", "backend-rest");
    }

    @Test
    public void contextLoads() {}

}
