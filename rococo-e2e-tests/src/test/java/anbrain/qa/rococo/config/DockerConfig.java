package anbrain.qa.rococo.config;

import lombok.NonNull;


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
        return "http://auth.niffler.dc:9000/";
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
    public String kafkaAddress() {
        return "kafka:9092";
    }

}