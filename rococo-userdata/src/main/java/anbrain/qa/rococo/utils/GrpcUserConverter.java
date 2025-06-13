package anbrain.qa.rococo.utils;

import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;


public class GrpcUserConverter {

    @Nonnull
    public static UserResponse convertToGrpcResponse(@Nonnull UserJson userJson) {
        return UserResponse.newBuilder()
                .setId(userJson.id().toString())
                .setUsername(userJson.username())
                .setFirstname(userJson.firstname() != null ? userJson.firstname() : "")
                .setLastname(userJson.lastname() != null ? userJson.lastname() : "")
                .setAvatar(userJson.avatar() != null ? userJson.avatar() : "")
                .build();
    }

}
