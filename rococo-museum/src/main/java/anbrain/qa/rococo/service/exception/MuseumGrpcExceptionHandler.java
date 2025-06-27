package anbrain.qa.rococo.service.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.CannotCreateTransactionException;
import lombok.extern.slf4j.Slf4j;


@GrpcAdvice
@Slf4j
public class MuseumGrpcExceptionHandler {

    private static final String SERVICE_UNAVAILABLE = "Сервис музеев временно недоступен";
    private static final String NOT_FOUND_TEMPLATE = "%s с ID %s не найден";
    private static final String INTERNAL_ERROR = "Внутренняя ошибка сервиса музеев";
    private static final String ALREADY_EXISTS = "Музей с такими параметрами уже существует";
    private static final String INVALID_UUID_FORMAT = "Некорректный формат UUID: %s";

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleEntityNotFound(@Nonnull EntityNotFoundException e) {
        String message = e.getMessage();
        String entityType = message.contains("Музей") ? "Музей" : "Страна";
        String entityId = extractIdFromException(message);

        log.warn("{} не найден: {}", entityType, entityId);
        return Status.NOT_FOUND
                .withDescription(String.format(NOT_FOUND_TEMPLATE, entityType, entityId))
                .asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgument(@Nonnull IllegalArgumentException e) {
        if (e.getMessage().contains("Invalid UUID string")) {
            String invalidUuid = extractInvalidUuid(e.getMessage());
            log.warn("Некорректный формат UUID: {}", invalidUuid);
            return Status.INVALID_ARGUMENT
                    .withDescription(String.format(INVALID_UUID_FORMAT, invalidUuid))
                    .asRuntimeException();
        }
        log.warn("Ошибка валидации: {}", e.getMessage());
        return Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException();
    }

    @GrpcExceptionHandler(DataIntegrityViolationException.class)
    public StatusRuntimeException handleDataIntegrityViolation(@Nonnull DataIntegrityViolationException e) {
        log.warn("Конфликт данных музея: {}", e.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ALREADY_EXISTS)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(CannotCreateTransactionException.class)
    public StatusRuntimeException handleServiceUnavailable() {
        log.error("Сервис музеев недоступен");
        return Status.UNAVAILABLE
                .withDescription(SERVICE_UNAVAILABLE)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleGeneric(Exception e) {
        log.error("Внутренняя ошибка сервиса музеев: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription(INTERNAL_ERROR)
                .asRuntimeException();
    }

    @Nonnull
    private String extractIdFromException(@Nonnull String message) {
        return message.replaceAll(".*id:\\s?([^\\s]+).*", "$1");
    }

    @Nonnull
    private String extractInvalidUuid(@Nonnull String message) {
        return message.replaceAll(".*'(.*)'.*", "$1");
    }
}