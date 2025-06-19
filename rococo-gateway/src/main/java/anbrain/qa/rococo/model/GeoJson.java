package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@Schema(description = "Географическая информация")
public record GeoJson(
        @Schema(description = "Город", example = "Амстердам")
        @JsonProperty("city")
        @Size(max = 50, message = "Название города не может превышать 50 символов")
        String city,
        @Schema(description = "Страна", implementation = CountryJson.class)
        @JsonProperty("country")
        @Valid
        CountryJson country
) {
    public GeoJson {
        if (city != null) city = city.trim();
    }
}
