package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Страна")
public record CountryJson(
        @Schema(description = "Уникальный идентификатор страны", example = "550e8400-e29b-41d4-a716-446655440002"
        )
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Название страны", example = "Нидерланды"
        )
        @JsonProperty("name")
        @Size(max = 255, message = "Название страны не может превышать 255 символов")
        String name
) {
    public CountryJson {
        if (name != null) {
            name = name.trim();
        }
    }
}
