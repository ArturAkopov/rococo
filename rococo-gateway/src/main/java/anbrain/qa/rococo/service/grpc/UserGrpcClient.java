package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.exception.NotFoundException;
import anbrain.qa.rococo.exception.ServiceUnavailableException;
import anbrain.qa.rococo.exception.ValidationException;
import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserGrpcClient {

    @GrpcClient("rococo-userdata")
    private UserdataGrpc.UserdataBlockingStub userServiceStub;

    public UserJson getUser(String username) {
        try {
            UserResponse response = userServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .getUser(UserRequest.newBuilder()
                            .setUsername(username)
                            .build());

            if (response.getId().isEmpty()) {
                throw new NotFoundException("User not found: " + username);
            }

            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, username); // Передаем username для контекста
        }
    }

    public UserJson updateUser(String username, @Nonnull UserJson updateRequest) {
        try {
            UpdateUserRequest request = UpdateUserRequest.newBuilder()
                    .setUsername(username)
                    .setFirstname(updateRequest.firstname())
                    .setLastname(updateRequest.lastname())
                    .setAvatar(updateRequest.avatar())
                    .build();

            UserResponse response = userServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)
                    .updateUser(request);

            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, username);
        }
    }

    @Nonnull
    private RuntimeException handleGrpcException(
            @Nonnull StatusRuntimeException e,
            @Nonnull String username
    ) {
        return switch (e.getStatus().getCode()) {
            case NOT_FOUND -> new NotFoundException("User not found: " + username);
            case INVALID_ARGUMENT -> new ValidationException("Invalid request for user: " + username);
            case PERMISSION_DENIED -> new AccessDeniedException("Access denied for user: " + username);
            case UNAVAILABLE -> new ServiceUnavailableException("User service unavailable");
            case DEADLINE_EXCEEDED -> new ServiceUnavailableException("User service timeout");
            default -> new RuntimeException("Error processing user: " + username, e);
        };
    }

    @Nonnull
    private UserJson convertToUserJson(@Nonnull UserResponse response) {
        return new UserJson(
                UUID.fromString(response.getId()),
                response.getUsername(),
                response.getFirstname(),
                response.getLastname(),
                response.getAvatar()
        );
    }
}
