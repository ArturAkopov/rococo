package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Музей")
public record MuseumJson(
        @Schema(description = "Уникальный идентификатор музея", example = "550e8400-e29b-41d4-a716-446655440001")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Название музея", example = "Музей Ван Гога")
        @JsonProperty("title")
        String title,
        @Schema(description = "Описание музея", example = "Художественный музей в Амстердаме, хранящий крупнейшую коллекцию картин Винсента Ван Гога")
        @JsonProperty("description")
        String description,
        @Schema(description = "Фотография музея", example = "data:image/jpeg;base64,/9j/2wBDAAQDAwQDAwQEAw...")
        @JsonProperty("photo")
        String photo,
        @Schema(description = "Географическое расположение музея", implementation = GeoJson.class)
        @JsonProperty("geo")
        GeoJson geo
) {
}
