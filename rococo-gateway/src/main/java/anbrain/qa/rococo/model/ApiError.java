package anbrain.qa.rococo.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

/**
 * Example response:
 * {
 * "timestamp": "2024-02-21T10:30:45.123Z",
 * "status": 404,
 * "error": "Not Found",
 * "message": "Painting not found",
 * "path": "/api/paintings/123"
 * }
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final Instant timestamp = Instant.now();
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    @Setter
    private List<ValidationError> validationErrors;

    public ApiError(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public record ValidationError(String field, String message) {
    }
}