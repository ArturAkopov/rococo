package anbrain.qa.rococo.model.rest;

import anbrain.qa.rococo.grpc.PaintingResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.UUID;

public record PaintingJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum_id")
        String museumId,
        @JsonProperty("artist_id")
        String artistId
) {
    @NonNull
    public static PaintingJson fromGrpcResponse(@Nullable PaintingResponse response) {

        return new PaintingJson(
                response != null ? UUID.fromString(response.getId()) : UUID.randomUUID(),
                response != null ? response.getTitle() : "",
                response != null ? response.getDescription() : "",
                response != null ? response.getContent() : "",
                response != null ? response.getMuseumId() : "",
                response != null ? response.getArtistId() : ""
        );
    }
}
