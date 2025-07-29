package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record MuseumJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("title")
        @Size(max = 255, message = "Название музея не может превышать 255 символов")
        String title,
        @JsonProperty("description")
        @Size(max = 2000, message = "Описание музея не может превышать 1000 символов")
        String description,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("geo")
        @Valid
        GeoJson geo
) {
        public MuseumJson {
                if (title != null) title = title.trim();
                if (description != null) description = description.trim();
        }
}
