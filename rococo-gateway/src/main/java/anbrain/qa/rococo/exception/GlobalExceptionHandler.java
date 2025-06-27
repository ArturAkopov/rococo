package anbrain.qa.rococo.exception;

import anbrain.qa.rococo.controller.error.ApiError;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            RococoBadRequestException.class,
            ConstraintViolationException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            RuntimeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBadRequestExceptions(Exception ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);

        if (ex instanceof RuntimeException runtimeEx &&
            runtimeEx.getMessage().contains("UNIMPLEMENTED")) {
            return buildErrorResponse(servletRequest, HttpStatus.BAD_REQUEST, ex.getMessage());
        }
        else if (ex instanceof RuntimeException && !(ex instanceof IllegalArgumentException)) {
            return handleInternalError(ex, servletRequest);
        }

        return buildErrorResponse(servletRequest, HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(RococoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFoundException(RococoNotFoundException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return buildErrorResponse(servletRequest, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RococoAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ApiError> handleAccessDeniedException(RococoAccessDeniedException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return buildErrorResponse(servletRequest, HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(RococoConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflictException(RococoConflictException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return buildErrorResponse(servletRequest, HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RococoServiceUnavailableException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResponseEntity<ApiError> handleUnavailableException(RococoServiceUnavailableException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return buildErrorResponse(servletRequest, HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleInternalError(Exception ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return buildErrorResponse(
                servletRequest,
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Произошла внутренняя ошибка сервера " + ex.getMessage()
        );
    }

    @Nonnull
    private ResponseEntity<ApiError> buildErrorResponse(
            HttpServletRequest servletRequest,
            HttpStatus status,
            String message
    ) {
        return new ResponseEntity<>(
                new ApiError(
                        status.toString(),
                        servletRequest.getRequestURI(),
                        status.getReasonPhrase(),
                        message
                ),
                status
        );
    }
}