package anbrain.qa.rococo.config;

import lombok.NonNull;

import java.util.Objects;


enum DockerConfig implements Config {
    INSTANCE;

    @NonNull
    @Override
    public String frontUrl() {
        return "http://frontend.rococo.dc/";
    }

    @NonNull
    @Override
    public String authUrl() {
        return "http://auth.rococo.dc:9000/";
    }

    @NonNull
    @Override
    public String gatewayUrl() {
        return "http://gateway.rococo.dc:8080/";
    }

    @NonNull
    @Override
    public String userdataGrpcAddress() {
        return "userdata.rococo.dc";
    }

    @NonNull
    @Override
    public String artistGrpcAddress() {
        return "artist.rococo.dc";
    }

    @NonNull
    @Override
    public String museumGrpcAddress() {
        return "museum.rococo.dc";
    }

    @NonNull
    @Override
    public String paintingGrpcAddress() {
        return "painting.rococo.dc";
    }

    @NonNull
    @Override
    public String kafkaAddress() {
        return "kafka:9092";
    }

    @Override
    public String allureDockerServiceUrl() {
        String allureDockerApiUrl = System.getenv("ALLURE_DOCKER_API");
        return Objects.requireNonNullElse(allureDockerApiUrl, "http://allure:5050/");
    }

    @NonNull
    @Override
    public String screenshotBaseDir() {
        return "screenshots/selenoid/";
    }

}