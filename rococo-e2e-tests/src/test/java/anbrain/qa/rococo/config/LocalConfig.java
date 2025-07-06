package anbrain.qa.rococo.config;

import lombok.NonNull;

import javax.annotation.Nonnull;

enum LocalConfig implements Config {
    INSTANCE;

    @NonNull
    @Override
    public String frontUrl() {
        return "http://127.0.0.1:3000/";
    }

    @NonNull
    @Override
    public String authUrl() {
        return "http://127.0.0.1:9000/";
    }

    @NonNull
    @Override
    public String gatewayUrl() {
        return "http://127.0.0.1:8080/";
    }

    @NonNull
    @Override
    public String userdataGrpcAddress() {
        return "127.0.0.1";
    }

    @NonNull
    @Override
    public String artistGrpcAddress() {
        return "127.0.0.1";
    }

    @NonNull
    @Override
    public String museumGrpcAddress() {
        return "127.0.0.1";
    }

    @NonNull
    @Override
    public String paintingGrpcAddress() {
        return "127.0.0.1";
    }

    @NonNull
    @Override
    public String kafkaAddress() {
        return "127.0.0.1:9092";
    }

    @Nonnull
    @Override
    public String allureDockerServiceUrl() {
        return "http://127.0.0.1:5050/";
    }

}
