package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.config.Config;
import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.rest.UserJson;
import anbrain.qa.rococo.utils.GrpcConsoleInterceptor;
import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import lombok.NonNull;


public class UserdataGrpcClient {

    private final Config CFG = Config.getInstance();

    private final Channel channel = ManagedChannelBuilder
            .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
            .usePlaintext()
            .intercept(new GrpcConsoleInterceptor())
            .build();

    private final UserdataGrpc.UserdataBlockingStub blockingStub
            = UserdataGrpc.newBlockingStub(channel);

    public UserJson getUser(String username) {
        final UserResponse response = blockingStub.getUser(UserRequest.newBuilder()
                .setUsername(username).build());
        return UserJson.fromGrpcResponse(response);
    }

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
