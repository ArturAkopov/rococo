package anbrain.qa.rococo.config;

import anbrain.qa.rococo.service.error.RococoErrorAttributes;
import io.grpc.ManagedChannelBuilder;
import net.devh.boot.grpc.client.channelfactory.GrpcChannelConfigurer;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean
    public ErrorAttributes errorAttributes() {
        return new RococoErrorAttributes();
    }

    @Bean
    public GrpcChannelConfigurer keepAliveClientConfigure() {
        return (channelBuilder, name) -> {
            if (channelBuilder != null) {
                ((ManagedChannelBuilder<?>) channelBuilder)
                        .keepAliveTime(30, TimeUnit.SECONDS)
                        .keepAliveTimeout(10, TimeUnit.SECONDS)
                        .idleTimeout(60, TimeUnit.SECONDS);
            }
        };
    }
}
