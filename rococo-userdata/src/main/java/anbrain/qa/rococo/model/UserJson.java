package anbrain.qa.rococo.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(max = 50, message = "Имя пользователя не может превышать 50 символов")
        String username,
        @JsonProperty("firstname")
        @Size(max = 255, message = "Имя не может превышать 100 символов")
        String firstname,
        @JsonProperty("lastname")
        @Size(max = 255, message = "Фамилия не может превышать 100 символов")
        String lastname,
        @JsonProperty("avatar")
        String avatar
) {
}
