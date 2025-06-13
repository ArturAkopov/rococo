package anbrain.qa.rococo;

import anbrain.qa.rococo.service.PropertiesLogger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RococoUserdataApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RococoUserdataApplication.class);
        springApplication.addListeners(new PropertiesLogger());
        springApplication.run(args);
    }
}