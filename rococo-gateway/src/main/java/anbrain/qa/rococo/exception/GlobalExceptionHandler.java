package anbrain.qa.rococo.exception;

import anbrain.qa.rococo.model.ApiError;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Hidden
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllExceptions(
            Exception ex,
            @Nonnull HttpServletRequest request
    ) {
        log.error("Unhandled exception: {} {}", request.getRequestURI(), ex.getMessage(), ex);
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiError> handleGrpcException(
            @Nonnull StatusRuntimeException ex,
            @Nonnull HttpServletRequest request
    ) {
        HttpStatus status = mapGrpcStatusToHttp(ex.getStatus());
        log.warn("gRPC error [{}]: {}", status.value(), ex.getMessage());

        return buildErrorResponse(
                status,
                status.getReasonPhrase(),
                extractGrpcErrorMessage(ex),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ApiError> handleTimeoutException(
            @Nonnull TimeoutException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Timeout: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.GATEWAY_TIMEOUT,
                "Gateway Timeout",
                "Request timed out",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(
            @Nonnull NotFoundException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Not found: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiError> handleValidationException(
            @Nonnull RuntimeException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Validation error: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(
            @Nonnull MethodArgumentNotValidException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Validation error: {} {}", request.getRequestURI(), ex.getMessage());

        List<ApiError.ValidationError> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapToValidationError)
                .toList();

        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Invalid request parameters",
                request.getRequestURI()
        );
        apiError.setValidationErrors(errors);

        return ResponseEntity.badRequest().body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(
            @Nonnull BadCredentialsException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Auth failed: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Invalid credentials",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(
            @Nonnull AccessDeniedException ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Access denied: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "Access denied",
                request.getRequestURI()
        );
    }

    // Вспомогательные методы
    private HttpStatus mapGrpcStatusToHttp(@Nonnull Status status) {
        return switch (status.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private String extractGrpcErrorMessage(@Nonnull StatusRuntimeException ex) {
        return ex.getStatus().getDescription() != null
                ? ex.getStatus().getDescription()
                : "gRPC service error";
    }

    @Nonnull
    private ResponseEntity<ApiError> buildErrorResponse(
            HttpStatus status,
            String error,
            String message,
            String path
    ) {
        return ResponseEntity
                .status(status)
                .body(new ApiError(status.value(), error, message, path));
    }

    @Nonnull
    private ApiError.ValidationError mapToValidationError(@Nonnull FieldError fieldError) {
        return new ApiError.ValidationError(
                fieldError.getField(),
                fieldError.getDefaultMessage() != null
                        ? fieldError.getDefaultMessage()
                        : "Invalid value"
        );
    }
}