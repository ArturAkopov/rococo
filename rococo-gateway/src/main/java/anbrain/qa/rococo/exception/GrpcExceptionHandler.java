package anbrain.qa.rococo.exception;

import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;

public class GrpcExceptionHandler {
    private static final String SERVICE_UNAVAILABLE = "Сервис временно недоступен";
    private static final String TIMEOUT_MESSAGE = "Превышено время ожидания ответа от сервиса";
    private static final String VALIDATION_ERROR = "Ошибка валидации данных";
    private static final String NOT_FOUND_TEMPLATE = "%s с ID %s не найден";
    private static final String ACCESS_DENIED = "Доступ запрещен";

    @Nonnull
    public static RuntimeException handleGrpcException(
            @Nonnull StatusRuntimeException e,
            @Nonnull String entityType,
            @Nonnull String entityId
    ) {
        return switch (e.getStatus().getCode()) {
            case NOT_FOUND -> new RococoNotFoundException(
                    String.format(NOT_FOUND_TEMPLATE, entityType, entityId));
            case INVALID_ARGUMENT -> new ValidationException(
                    String.format("%s: %s - %s", VALIDATION_ERROR, entityType, entityId));
            case UNAVAILABLE -> new ServiceUnavailableException(SERVICE_UNAVAILABLE);
            case DEADLINE_EXCEEDED -> new ServiceUnavailableException(TIMEOUT_MESSAGE);
            case PERMISSION_DENIED -> new AccessDeniedException(ACCESS_DENIED);
            case ALREADY_EXISTS -> new RococoConflictException(
                    String.format("%s с такими параметрами уже существует: %s", entityType, entityId));
            default -> new RuntimeException(
                    String.format("Ошибка при обработке %s: %s - %s", entityType, entityId, e.getStatus().getDescription()));
        };
    }
}