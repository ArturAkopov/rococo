package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Географическая информация")
public record GeoJson(
        @Schema(description = "Город", example = "Амстердам")
        @JsonProperty("city")
        String city,
        @Schema(description = "Страна", implementation = CountryJson.class)
        @JsonProperty("country")
        CountryJson country
) {
}
