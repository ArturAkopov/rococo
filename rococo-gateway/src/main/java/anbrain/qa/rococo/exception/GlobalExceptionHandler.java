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

    // Обработка всех необработанных исключений
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

    // Обработка gRPC исключений, которые не были преобразованы GrpcExceptionHandler
    @ExceptionHandler(StatusRuntimeException.class)
    public ResponseEntity<ApiError> handleUnmappedGrpcException(
            @Nonnull StatusRuntimeException ex,
            @Nonnull HttpServletRequest request
    ) {
        HttpStatus status = mapGrpcStatusToHttp(ex.getStatus());
        String message = extractGrpcErrorMessage(ex);

        log.warn("Unhandled gRPC error [{}] for {}: {}", status.value(), request.getRequestURI(), message);

        return buildErrorResponse(
                status,
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    // Обработка проблем доступности сервиса
    @ExceptionHandler({
            TimeoutException.class,
            ServiceUnavailableException.class
    })
    public ResponseEntity<ApiError> handleServiceUnavailable(
            @Nonnull Exception ex,
            @Nonnull HttpServletRequest request
    ) {
        log.warn("Service unavailable: {} {}", request.getRequestURI(), ex.getMessage());
        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    // Обработка ошибок валидации DTO
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

    // Обработка ошибок аутентификации
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

    // Обработка прочих ошибок валидации
    @ExceptionHandler({
            ValidationException.class,
            IllegalArgumentException.class,
            IllegalStateException.class
    })
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

    private HttpStatus mapGrpcStatusToHttp(@Nonnull Status status) {
        return switch (status.getCode()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_ARGUMENT, FAILED_PRECONDITION -> HttpStatus.BAD_REQUEST;
            case UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case PERMISSION_DENIED -> HttpStatus.FORBIDDEN;
            case UNAVAILABLE, ABORTED -> HttpStatus.SERVICE_UNAVAILABLE;
            case DEADLINE_EXCEEDED -> HttpStatus.GATEWAY_TIMEOUT;
            case ALREADY_EXISTS -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private String extractGrpcErrorMessage(@Nonnull StatusRuntimeException ex) {
        return ex.getStatus().getDescription() != null
                ? ex.getStatus().getDescription()
                : "Service error: " + ex.getStatus().getCode().name();
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