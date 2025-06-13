package anbrain.qa.rococo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleGrpcNotFound(@Nonnull EntityNotFoundException e) {
        log.error("gRPC Entity not found: {}", e.getMessage());
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public StatusRuntimeException handleGrpcDataError(@Nonnull DataIntegrityViolationException e) {
        log.error("gRPC Data integrity violation: {}", e.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription("Data conflict: " + e.getRootCause().getMessage())
                .asRuntimeException();
    }

    @ExceptionHandler(Exception.class)
    public StatusRuntimeException handleGrpcError(Exception e) {
        log.error("gRPC Internal error: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription("Internal server error")
                .withCause(e)
                .asRuntimeException();
    }

    record ErrorResponse(String error, String message) {
    }
}