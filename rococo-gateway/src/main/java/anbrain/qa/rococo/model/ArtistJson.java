package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Художник")
public record ArtistJson(
        @Schema(description = "Уникальный идентификатор", example = "550e8400-e29b-41d4-a716-446655440000")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Имя художника", example = "Винсент Ван Гог")
        @JsonProperty("name")
        String name,
        @Schema(description = "Биография художника", example = "Нидерландский художник-постимпрессионист, автор знаменитых картин «Звёздная ночь» и «Подсолнухи»")
        @JsonProperty("biography")
        String biography,
        @Schema(description = "Фотография художника", example = "data:image/jpeg;base64,/9j/2wBDAAQDAwQDAwQEAw...")
        @JsonProperty("photo")
        String photo
) {
}
