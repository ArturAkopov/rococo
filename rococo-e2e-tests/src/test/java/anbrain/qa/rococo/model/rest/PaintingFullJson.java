package anbrain.qa.rococo.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record PaintingFullJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        String title,
        @JsonProperty("description")
        String description,
        @JsonProperty("content")
        String content,
        @JsonProperty("museum")
        MuseumJson museum,
        @JsonProperty("artist")
        ArtistJson artist
) {
}
