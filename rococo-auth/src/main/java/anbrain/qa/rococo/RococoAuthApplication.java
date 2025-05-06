package anbrain.qa.rococo;

import anbrain.qa.rococo.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;

public class RococoAuthApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoAuthApplication.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}
