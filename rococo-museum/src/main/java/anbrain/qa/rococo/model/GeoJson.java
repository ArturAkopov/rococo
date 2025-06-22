package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record GeoJson(
        @JsonProperty("city")
        @Size(max = 50, message = "Название города не может превышать 50 символов")
        String city,
        @JsonProperty("country")
        @Valid
        CountryJson country
) {
        public GeoJson {
                if (city != null) city = city.trim();
        }
}
