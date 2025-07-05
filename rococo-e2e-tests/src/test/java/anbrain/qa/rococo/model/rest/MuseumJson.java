package anbrain.qa.rococo.model.rest;

import anbrain.qa.rococo.grpc.MuseumResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("geo")
        GeoJson geo
) {
    @NonNull
    public static MuseumJson fromGrpcResponse(@Nullable MuseumResponse response) {

        return new MuseumJson(
                response != null ? UUID.fromString(response.getId()) : UUID.randomUUID(),
                response != null ? response.getTitle() : "",
                response != null ? response.getDescription() : "",
                response != null ? response.getPhoto() : "",
                new GeoJson(
                        response != null ? response.getGeo().getCity() : "",
                        new CountryJson(
                                response != null ? UUID.fromString(response.getGeo().getCountry().getId()) : UUID.randomUUID(),
                                response != null ? response.getGeo().getCountry().getName() : ""
                        )
                )
        );
    }
}
