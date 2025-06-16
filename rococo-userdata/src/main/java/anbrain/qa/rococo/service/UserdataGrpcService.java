package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;


import static anbrain.qa.rococo.utils.GrpcUserConverter.convertToGrpcResponse;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserdataGrpcService extends UserdataGrpc.UserdataImplBase {

    private final UserdataDatabaseService userdataDatabaseService;

    @Transactional(readOnly = true)
    @Override
    public void getUser(@NonNull UserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        log.debug("Getting user by username: {}", request.getUsername());
        UserJson userJson = userdataDatabaseService.getUserByUsername(request.getUsername());
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(@NonNull UpdateUserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        log.debug("Updating user: {}", request.getUsername());
        UserJson userJson = userdataDatabaseService.updateUser(
                request.getUsername(),
                request.getFirstname(),
                request.getLastname(),
                request.getAvatar()
        );
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }
}