package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Картина")
public record PaintingJson(
        @Schema(description = "Уникальный идентификатор картины", example = "550e8400-e29b-41d4-a716-446655440000")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Название картины", example = "Звёздная ночь")
        @JsonProperty("title")
        String title,
        @Schema(description = "Описание картины", example = "Одна из самых известных картин Ван Гога, написанная в 1889 году")
        @JsonProperty("description")
        String description,
        @Schema(description = "Изображения картины", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...")
        @JsonProperty("content")
        String content,
        @Schema(description = "Музей, где хранится картина", implementation = MuseumJson.class)
        @JsonProperty("museum")
        MuseumJson museum,
        @Schema(description = "Художник, создавший картину", implementation = ArtistJson.class)
        @JsonProperty("artist")
        ArtistJson artist
) {

}
