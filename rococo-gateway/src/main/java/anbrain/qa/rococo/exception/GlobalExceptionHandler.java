package anbrain.qa.rococo.exception;

import anbrain.qa.rococo.controller.error.ApiError;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RococoNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFoundException(RococoNotFoundException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.NOT_FOUND.toString(),
                        servletRequest.getRequestURI(),
                        "Not found",
                        ex.getMessage()
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(RococoBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleBadRequestException(RococoBadRequestException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.BAD_REQUEST.toString(),
                        servletRequest.getRequestURI(),
                        "Bad request",
                        ex.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(RococoConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleConflictException(RococoConflictException ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.CONFLICT.toString(),
                        servletRequest.getRequestURI(),
                        "Conflict",
                        ex.getMessage()
                ),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ApiError> handleInternalError(Exception ex, @Nonnull HttpServletRequest servletRequest) {
        log.error(servletRequest.getRequestURI(), ex);
        return new ResponseEntity<>(
                new ApiError(
                        HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                        servletRequest.getRequestURI(),
                        "Internal server error",
                        "Произошла внутренняя ошибка сервера"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}