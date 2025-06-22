package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ArtistJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("name")
        @Size(max = 255, message = "Имя художника не может превышать 255 символов")
        String name,
        @JsonProperty("biography")
        @Size(max = 2000, message = "Биография художника не может превышать 2000 символов")
        String biography,
        @JsonProperty("photo")
        String photo
) {
        public ArtistJson {
                if (name != null) {
                        name = name.trim();
                }
                if (biography != null) {
                        biography = biography.trim();
                }
        }
}
