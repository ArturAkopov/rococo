package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Slf4j
@Service
public class UserGrpcClient {

    @GrpcClient("rococo-userdata")
    private UserdataGrpc.UserdataBlockingStub userServiceStub;

    public UserJson getUser(String username) {
        try {
            log.info("Запрос пользователя с username: {}", username);

            UserResponse response = userServiceStub
                    .getUser(UserRequest.newBuilder()
                            .setUsername(username)
                            .build());

            if (response.getId().isEmpty()) {
                log.error("Пользователь с username {} не найден", username);
                throw handleGrpcException(
                        new StatusRuntimeException(io.grpc.Status.NOT_FOUND),
                        "Пользователь",
                        username
                );
            }

            log.info("Пользователь {} успешно получен", username);
            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении пользователя {}: {}", username, e.getStatus().getDescription());
            throw handleGrpcException(e, "Пользователь", username);
        }
    }

    public UserJson updateUser(String username, @Nonnull UserJson updateRequest) {
        try {
            log.info("Обновление пользователя {}, данные: {}", username, updateRequest);

            UpdateUserRequest request = UpdateUserRequest.newBuilder()
                    .setUsername(username)
                    .setFirstname(updateRequest.firstname())
                    .setLastname(updateRequest.lastname())
                    .setAvatar(updateRequest.avatar())
                    .build();

            UserResponse response = userServiceStub
                    .updateUser(request);

            log.info("Пользователь {} успешно обновлен", username);
            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при обновлении пользователя {}: {}", username, e.getStatus().getDescription());
            throw handleGrpcException(e, "Обновление пользователя", username);
        }
    }

    @Nonnull
    public UserJson convertToUserJson(@Nonnull UserResponse response) {
        return new UserJson(
                UUID.fromString(response.getId()),
                response.getUsername(),
                response.getFirstname(),
                response.getLastname(),
                response.getAvatar()
        );
    }
}