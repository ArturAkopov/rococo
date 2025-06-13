package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;

import static anbrain.qa.rococo.utils.GrpcUserConverter.convertToGrpcResponse;

@GrpcService
public class UserdataGrpcService extends UserdataGrpc.UserdataImplBase {

    private final UserdataService userdataService;

    @Autowired
    public UserdataGrpcService(UserdataService userdataService) {
        this.userdataService = userdataService;
    }

    @Override
    public void getUser(@NonNull UserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        UserJson userJson = userdataService.getUser(request.getUsername());
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(@NonNull UpdateUserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        UserJson userJson = userdataService.updateUser(request);
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }
}