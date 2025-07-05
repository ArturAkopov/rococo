package anbrain.qa.rococo.model.rest;

import anbrain.qa.rococo.grpc.ArtistResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        String name,
        @JsonProperty("biography")
        String biography,
        @JsonProperty("photo")
        String photo
) {
    @NonNull
    public static ArtistJson fromGrpcResponse(@Nullable ArtistResponse response) {

        return new ArtistJson(
                response != null ? UUID.fromString(response.getId()) : UUID.randomUUID(),
                response != null ? response.getName() : "",
                response != null ? response.getBiography() : "",
                response != null ? response.getPhoto() : ""
        );
    }
}
