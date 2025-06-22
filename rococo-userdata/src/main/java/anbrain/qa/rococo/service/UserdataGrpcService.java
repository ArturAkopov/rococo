package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserdataGrpcService extends UserdataGrpc.UserdataImplBase {

    private final UserdataDatabaseService userdataDatabaseService;

    @Transactional(readOnly = true)
    @Override
    public void getUser(@NonNull UserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
            log.debug("Получение пользователя: {}", request.getUsername());

            UserProfileEntity userEntity = userdataDatabaseService.getUserByUsername(request.getUsername());
            log.info("Пользователь {} успешно найден", request.getUsername());

            responseObserver.onNext(convertEntityToGrpcResponse(userEntity));
            responseObserver.onCompleted();
            log.debug("Запрос getUser успешно завершен");
    }

    @Override
    public void updateUser(@NonNull UpdateUserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
            log.debug("Обновление пользователя: {}", request.getUsername());

            if (request.getFirstname().isBlank() || request.getLastname().isBlank()) {
                throw new IllegalArgumentException("Имя и фамилия обязательны для заполнения");
            }

            UserProfileEntity updatedEntity = userdataDatabaseService.updateUser(
                    request.getUsername(),
                    request.getFirstname(),
                    request.getLastname(),
                    request.getAvatar()
            );

            log.info("Пользователь {} успешно обновлен", request.getUsername());
            responseObserver.onNext(convertEntityToGrpcResponse(updatedEntity));
            responseObserver.onCompleted();
            log.debug("Запрос updateUser успешно завершен");
    }

    @Nonnull
    private UserResponse convertEntityToGrpcResponse(@Nonnull UserProfileEntity entity) {
        return UserResponse.newBuilder()
                .setId(entity.getId().toString())
                .setUsername(entity.getUsername())
                .setFirstname(entity.getFirstname() != null ? entity.getFirstname() : "")
                .setLastname(entity.getLastname() != null ? entity.getLastname() : "")
                .setAvatar(entity.getAvatar() != null ? new String(entity.getAvatar(), StandardCharsets.UTF_8) : "")
                .build();
    }
}