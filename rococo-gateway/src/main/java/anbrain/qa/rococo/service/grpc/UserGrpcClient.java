package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static anbrain.qa.rococo.utils.GrpcExceptionHandler.handleGrpcException;

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
                throw handleGrpcException(new StatusRuntimeException(Status.NOT_FOUND),"User",username);
            }

            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e, "User", username);
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
            throw handleGrpcException(e, "User", username);
        }
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
