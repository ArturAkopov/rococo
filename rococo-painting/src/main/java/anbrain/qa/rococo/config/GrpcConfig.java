//package anbrain.qa.rococo.config;
//import net.devh.boot.grpc.client.config.GrpcChannelsProperties;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import net.devh.boot.grpc.client.config.GrpcChannelsProperties;
//import net.devh.boot.grpc.server.config.GrpcServerProperties;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GrpcConfig {
//
//    private final int serverPort;
//    private final int artistPort;
//    private final int museumPort;
//
//    @Autowired
//    public GrpcConfig(@Value("${spring.grpc.server.port}") int serverPort,
//                      @Value("${spring.grpc.client.rococo-artist.port}") int artistPort,
//                      @Value("${spring.grpc.client.rococo-museum.port}") int museumPort) {
//        this.serverPort = serverPort;
//        this.artistPort = artistPort;
//        this.museumPort = museumPort;
//    }
//
//    @Bean
//    public GrpcServerProperties grpcServerProperties() {
//        GrpcServerProperties properties = new GrpcServerProperties();
//        properties.setPort(serverPort);
//        return properties;
//    }
//
//    @Bean
//    public GrpcChannelsProperties grpcChannelsProperties() {
//        GrpcChannelsProperties properties = new GrpcChannelsProperties();
//
//        Map<String, GrpcChannelsProperties.ClientConfig> clientConfigs = new HashMap<>();
//
//        // Настройка artist клиента
//        GrpcChannelsProperties.ClientConfig artistConfig = new GrpcChannelsProperties.ClientConfig();
//        artistConfig.setAddress("static://localhost:" + artistPort);
//        clientConfigs.put("rococo-artist", artistConfig);
//
//        // Настройка museum клиента
//        GrpcChannelsProperties.ClientConfig museumConfig = new GrpcChannelsProperties.ClientConfig();
//        museumConfig.setAddress("static://localhost:" + museumPort);
//        clientConfigs.put("rococo-museum", museumConfig);
//
//        properties.setClient(clientConfigs);
//        return properties;
//    }
//}
