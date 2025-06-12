package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record UserJson(
        @Schema(description = "Уникальный идентификатор музея", example = "550e8400-e29b-41d4-a716-446655440001")
        @JsonProperty("id")
        UUID id,
        @Schema(description = "Логин пользователя", example = "testuser")
        @JsonProperty("username")
        String username,
        @Schema(description = "Имя пользователя", example = "Артур")
        @JsonProperty("firstname")
        String firstname,
        @Schema(description = "Фамилия пользователя", example = "Акопов")
        @JsonProperty("lastname")
        String lastname,
        @Schema(description = "Аватар пользователя", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIAAD...")
        @JsonProperty("avatar")
        String avatar
) {
}
