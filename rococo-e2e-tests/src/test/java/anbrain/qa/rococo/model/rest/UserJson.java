package anbrain.qa.rococo.model.rest;

import anbrain.qa.rococo.grpc.UserResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NonNull;

import javax.annotation.Nullable;
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
    public static UserJson fromGrpcResponse(@Nullable UserResponse response) {

        return new UserJson(
                response != null ? UUID.fromString(response.getId()) : UUID.randomUUID(),
                response != null ? response.getUsername() : "",
                response != null ? response.getFirstname() : "",
                response != null ? response.getLastname() : "",
                response != null ? response.getAvatar() : ""
        );
    }
}
