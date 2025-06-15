package anbrain.qa.rococo.exception;

import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;

public class GrpcExceptionHandler {

    @Nonnull
    public static RuntimeException handleGrpcException(
            @Nonnull StatusRuntimeException e,
            @Nonnull String entityType,
            @Nonnull String entityId
    ) {
        return switch (e.getStatus().getCode()) {
            case NOT_FOUND -> new NotFoundException(
                    String.format("%s not found with id: %s", entityType, entityId));
            case INVALID_ARGUMENT -> new ValidationException(
                    String.format("Invalid request for %s: %s", entityType, entityId));
            case UNAVAILABLE -> new ServiceUnavailableException(
                    String.format("%s service unavailable", entityType));
            case DEADLINE_EXCEEDED -> new ServiceUnavailableException(
                    String.format("%s service timeout", entityType));
            case PERMISSION_DENIED -> new AccessDeniedException(
                    String.format("%s access denied: %s", entityType, entityId));
            default -> new RuntimeException(
                    String.format("Error processing %s: %s", entityType, entityId), e);
        };
    }

}
