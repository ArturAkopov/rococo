package anbrain.qa.rococo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.lognet.springboot.grpc.recovery.GRpcExceptionHandler;
import org.lognet.springboot.grpc.recovery.GRpcServiceAdvice;

@GRpcServiceAdvice
public class GrpcExceptionHandler {

    @GRpcExceptionHandler
    public StatusRuntimeException handleNotFoundException(@Nonnull ArtistNotFoundException e) {
        return Status.NOT_FOUND
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GRpcExceptionHandler
    public StatusRuntimeException handleIllegalArgumentException(@Nonnull IllegalArgumentException e) {
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .withCause(e)
                .asRuntimeException();
    }

    @GRpcExceptionHandler
    public StatusRuntimeException handleAllExceptions(@Nonnull Exception e) {
        return Status.INTERNAL
                .withDescription("Internal server error")
                .withCause(e)
                .asRuntimeException();
    }
}
