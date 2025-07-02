package anbrain.qa.rococo.config;

import lombok.NonNull;

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
    public String userdataGrpcAddress() {
        return "127.0.0.1";
    }

    @NonNull
    @Override
    public String kafkaAddress() {
        return "127.0.0.1:9092";
    }

}
