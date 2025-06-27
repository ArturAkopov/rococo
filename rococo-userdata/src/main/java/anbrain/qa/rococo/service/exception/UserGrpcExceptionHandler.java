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
public class UserGrpcExceptionHandler {

    private static final String SERVICE_UNAVAILABLE = "Сервис пользователей временно недоступен";
    private static final String USER_NOT_FOUND = "Пользователь '%s' не найден";
    private static final String INTERNAL_ERROR = "Внутренняя ошибка сервиса пользователей";
    private static final String INVALID_DATA = "Некорректные данные пользователя: %s";

    @GrpcExceptionHandler(EntityNotFoundException.class)
    public StatusRuntimeException handleUserNotFound(@Nonnull EntityNotFoundException e) {
        String username = extractUsernameFromException(e.getMessage());
        log.warn("Пользователь не найден: {}", username);
        return Status.NOT_FOUND
                .withDescription(String.format(USER_NOT_FOUND, username))
                .asRuntimeException();
    }

    @GrpcExceptionHandler(IllegalArgumentException.class)
    public StatusRuntimeException handleIllegalArgument(@Nonnull IllegalArgumentException e) {
        log.warn("Ошибка валидации данных: {}", e.getMessage());
        return Status.INVALID_ARGUMENT
                .withDescription(String.format(INVALID_DATA, e.getMessage()))
                .asRuntimeException();
    }

    @GrpcExceptionHandler(DataIntegrityViolationException.class)
    public StatusRuntimeException handleDataIntegrityViolation(@Nonnull DataIntegrityViolationException e) {
        log.warn("Конфликт данных пользователя: {}", e.getMostSpecificCause().getMessage());
        return Status.ALREADY_EXISTS
                .withDescription("Пользователь с такими данными уже существует")
                .asRuntimeException();
    }

    @GrpcExceptionHandler(CannotCreateTransactionException.class)
    public StatusRuntimeException handleServiceUnavailable() {
        log.error("Сервис пользователей недоступен - ошибка подключения к БД");
        return Status.UNAVAILABLE
                .withDescription(SERVICE_UNAVAILABLE)
                .asRuntimeException();
    }

    @GrpcExceptionHandler
    public StatusRuntimeException handleGeneric(Exception e) {
        log.error("Необработанная ошибка в сервисе пользователей: {}", e.getMessage(), e);
        return Status.INTERNAL
                .withDescription(INTERNAL_ERROR)
                .asRuntimeException();
    }

    @Nonnull
    private String extractUsernameFromException(@Nonnull String message) {
        return message.replace("User not found: ", "");
    }
}