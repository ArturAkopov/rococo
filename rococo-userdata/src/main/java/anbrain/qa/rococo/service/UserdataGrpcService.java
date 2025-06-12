package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;

@GrpcService
public class UserdataGrpcService extends UserdataGrpc.UserdataImplBase {

    private final UserdataService userdataService;

    @Autowired
    public UserdataGrpcService(UserdataService userdataService) {
        this.userdataService = userdataService;
    }


    @Override
    public void getUser(@NonNull UserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        UserJson user = userdataService.getUser(request.getUsername());

        // Преобразуем UserJson в gRPC-ответ
        UserResponse response = UserResponse.newBuilder()
                .setId(user.id().toString())
                .setUsername(user.username())
                .setFirstname(user.firstname())
                .setLastname(user.lastname())
                .setAvatar(user.avatar())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
