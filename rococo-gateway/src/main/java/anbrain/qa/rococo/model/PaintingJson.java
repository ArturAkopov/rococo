package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Картина")
public record PaintingJson(
        @Schema(description = "Уникальный идентификатор картины", example = "550e8400-e29b-41d4-a716-446655440000")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Название картины", example = "Звёздная ночь")
        @JsonProperty("title")
        @Size(max = 255, message = "Название картины не может превышать 255 символов")
        @NotEmpty
        String title,
        @Schema(description = "Описание картины", example = "Одна из самых известных картин Ван Гога, написанная в 1889 году")
        @JsonProperty("description")
        @Size(max = 1000, message = "Описание картины не может превышать 1000 символов")
        String description,
        @Schema(description = "Изображения картины", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...")
        @JsonProperty("content")
        String content,
        @Schema(description = "Музей, где хранится картина", implementation = MuseumJson.class)
        @JsonProperty("museum")
        @Valid
        MuseumJson museum,
        @Schema(description = "Художник, создавший картину", implementation = ArtistJson.class)
        @JsonProperty("artist")
        @Valid
        ArtistJson artist
) {
    public PaintingJson {
        if (title != null) title = title.trim();
        if (description != null) description = description.trim();
    }
}
