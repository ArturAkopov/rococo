package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.exception.NotFoundException;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import javax.naming.ServiceUnavailableException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class UserGrpcClient {

    @GrpcClient("userdata-service")
    private UserdataGrpc.UserdataBlockingStub userServiceStub;

    public UserJson getUser(String username) {
        try {
            UserResponse response = userServiceStub
                    .withDeadlineAfter(5, TimeUnit.SECONDS)  // Таймаут 2 секунды
                    .getUser(UserRequest.newBuilder()
                            .setUsername(username)
                            .build());

            if (response.getId().isEmpty()) {
                throw new RuntimeException("User not found");
            }

            return convertToUserJson(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException("User not found: " + username);
            }
            try {
                throw new ServiceUnavailableException("User service unavailable");
            } catch (ServiceUnavailableException ex) {
                throw new RuntimeException(ex);
            }
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
