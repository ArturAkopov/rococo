package anbrain.qa.rococo.controller.error;

import jakarta.annotation.Nonnull;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ApiError {

    private final Error error;

    public ApiError(Error error) {
        this.error = error;
    }

    public ApiError(String code, String domain, String reason, String message) {
        this.error = new Error(
                code,
                List.of(new ErrorItems(
                                domain,
                                reason,
                                message
                        )
                ));
    }

    public Map<String, Object> toAttributesMap() {
        return Map.of(
                "error", error
        );
    }

    @Nonnull
    public static ApiError fromAttributesMap(@Nonnull Map<String, Object> attributesMap) {
        return new ApiError(
                ((Integer) attributesMap.get("status")).toString(),
                (String) attributesMap.getOrDefault("path", "Не найден path"),
                (String) attributesMap.getOrDefault("path", "Не найден reason"),
                (String) attributesMap.getOrDefault("error", "Не найден message")

        );
    }

    private record Error(String code, List<ErrorItems> errors) {
    }

    private record ErrorItems(String domain, String reason, String message) {
    }

}
