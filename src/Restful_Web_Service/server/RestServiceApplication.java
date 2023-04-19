package Restful_Web_Service.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RestServiceApplication {

    public static void main(String[] args) {
        ModelStorage.createSampleModel();

        SpringApplication.run(RestServiceApplication.class, args);
    }
}
