package anbrain.qa.rococo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GrpcExceptionHandler {

    @ExceptionHandler
    public StatusRuntimeException handleNotFoundException(@Nonnull ArtistNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @ExceptionHandler
    public StatusRuntimeException handleIllegalArgumentException(@Nonnull IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @ExceptionHandler
    public StatusRuntimeException handleAllExceptions(@Nonnull Exception e) {
        return Status.INTERNAL
                .withDescription("Internal server error")
                .withCause(e)
                .asRuntimeException();
    }
}
