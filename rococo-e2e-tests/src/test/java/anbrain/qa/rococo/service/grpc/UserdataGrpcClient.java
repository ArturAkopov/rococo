package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.utils.AllureGrpcInterceptor;
import anbrain.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.qameta.allure.Step;
import lombok.NonNull;


public class UserdataGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .intercept(new AllureGrpcInterceptor())
            .intercept(new GrpcConsoleInterceptor())
            .usePlaintext()
            .build();

    private final UserdataGrpc.UserdataBlockingStub blockingStub
            = UserdataGrpc.newBlockingStub(channel);

    @Step("Получение информации пользователя - {username} по grpc")
    public UserJson getUser(String username) {
        try {
            final UserResponse response = blockingStub.getUser(
                    UserRequest.newBuilder()
                            .setUsername(username)
                            .build()
            );

            if (response == null) {
                return null;
            }

            return UserJson.fromGrpcResponse(response);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    @Step("Обновление информации пользователя - {userJson.username} по grpc")
    public UserJson updateUser(@NonNull UserJson userJson) {
        final UserResponse response = blockingStub.updateUser(UpdateUserRequest.newBuilder()
                .setUsername(userJson.username())
                .setFirstname(userJson.firstname())
                .setLastname(userJson.lastname())
                .setAvatar(userJson.avatar())
                .build());
        return UserJson.fromGrpcResponse(response);
    }
}
