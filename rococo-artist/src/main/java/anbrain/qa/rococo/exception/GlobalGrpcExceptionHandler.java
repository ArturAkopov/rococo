package anbrain.qa.rococo.exception;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.CannotCreateTransactionException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@GrpcAdvice
@Slf4j
public class GlobalGrpcExceptionHandler {

    private static final String SERVICE_UNAVAILABLE = "Сервис художников временно недоступен";
    private static final String NOT_FOUND_TEMPLATE = "Художник с ID %s не найден";
    private static final String INTERNAL_ERROR = "Внутренняя ошибка сервиса художников";
    private static final String ALREADY_EXISTS = "Художник с такими параметрами уже существует";
    private static final String INVALID_UUID_FORMAT = "Некорректный формат UUID: %s";

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleEntityNotFound(EntityNotFoundException e) {
        String entityId = extractIdFromException(e.getMessage());
        log.warn("Художник не найден: {}", entityId);
        return Status.NOT_FOUND
                .withDescription(String.format(NOT_FOUND_TEMPLATE, entityId))
                .asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgument(IllegalArgumentException e) {
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
    public StatusRuntimeException handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.warn("Конфликт данных: {}", e.getMessage());
        return Status.ALREADY_EXISTS
                .withDescription(ALREADY_EXISTS)
                .asRuntimeException();
    }

    @GrpcExceptionHandler(CannotCreateTransactionException.class)
    public StatusRuntimeException handleServiceUnavailable() {
        log.error("Сервис художников недоступен");
        return Status.UNAVAILABLE
                .withDescription(SERVICE_UNAVAILABLE)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleGeneric(Exception e) {
        log.error("Внутренняя ошибка сервиса: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription(INTERNAL_ERROR)
                .asRuntimeException();
    }

    private String extractIdFromException(@Nonnull String message) {
        return Arrays.stream(message.split(" "))
                .filter(part -> part.matches(".*[a-fA-F0-9]{8}-.*"))
                .findFirst()
                .orElse("unknown");
    }

    private String extractInvalidUuid(@Nonnull String message) {
        return message.replaceAll(".*'(.*)'.*", "$1");
    }
}