package anbrain.qa.rococo.config;

import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import java.util.List;

public interface Config {

    static @NotNull Config getInstance() {
        return "docker".equals(System.getProperty("test.env"))
                ? DockerConfig.INSTANCE
                : LocalConfig.INSTANCE;
    }

    String frontUrl();

    String authUrl();

    String userdataGrpcAddress();

    String kafkaAddress();

    default List<String> kafkaTopics() {
        return List.of("users");
    }

    default int userdataGrpcPort() {
        return 9090;
    }

}
