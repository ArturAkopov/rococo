package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CountryJson(
        @JsonProperty("id")
        UUID id,
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
