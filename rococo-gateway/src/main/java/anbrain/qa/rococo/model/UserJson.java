package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UserJson(
        @Schema(description = "Уникальный идентификатор музея", example = "550e8400-e29b-41d4-a716-446655440001")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Логин пользователя", example = "testuser")
        @JsonProperty("username")
        @NotBlank(message = "Имя пользователя не может быть пустым")
        @Size(max = 50, message = "Имя пользователя не может превышать 50 символов")
        String username,
        @Schema(description = "Имя пользователя", example = "Артур")
        @JsonProperty("firstname")
        @Size(max = 100, message = "Имя не может превышать 100 символов")
        String firstname,
        @Schema(description = "Фамилия пользователя", example = "Акопов")
        @JsonProperty("lastname")
        @Size(max = 100, message = "Фамилия не может превышать 100 символов")
        String lastname,
        @Schema(description = "Аватар пользователя", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...")
        @JsonProperty("avatar")
        String avatar
) {
    public UserJson {
        if (firstname != null) firstname = firstname.trim();
        if (lastname != null) lastname = lastname.trim();
    }
}
