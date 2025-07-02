package anbrain.qa.rococo.model.rest;

import anbrain.qa.rococo.grpc.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("lastname")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {
    @NonNull
    public static UserJson fromGrpcResponse(@NonNull UserResponse response) {

        return new UserJson(
                UUID.fromString(response.getId()),
                response.getUsername(),
                response.getFirstname(),
                response.getLastname(),
                response.getAvatar()
        );
    }
}
