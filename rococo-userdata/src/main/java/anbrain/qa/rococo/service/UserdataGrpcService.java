package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.data.repository.UserProfileRepository;
import anbrain.qa.rococo.exception.UserNotFoundException;
import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.grpc.UserRequest;
import anbrain.qa.rococo.grpc.UserResponse;
import anbrain.qa.rococo.grpc.UserdataGrpc;
import anbrain.qa.rococo.model.UserJson;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static anbrain.qa.rococo.utils.GrpcUserConverter.convertToGrpcResponse;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class UserdataGrpcService extends UserdataGrpc.UserdataImplBase {

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    @Override
    public void getUser(@NonNull UserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        log.debug("Getting user by username: {}", request.getUsername());
        UserJson userJson = userProfileRepository.findByUsername(request.getUsername())
                .map(this::toUserJson)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUsername()));
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }

    @Override
    public void updateUser(@NonNull UpdateUserRequest request, @NonNull StreamObserver<UserResponse> responseObserver) {
        log.debug("Updating user: {}", request.getUsername());
        UserProfileEntity entity = userProfileRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUsername()));
        updateEntityFromRequest(entity, request);
        UserJson userJson = toUserJson(userProfileRepository.save(entity));
        responseObserver.onNext(convertToGrpcResponse(userJson));
        responseObserver.onCompleted();
    }

    @Nonnull
    private UserJson toUserJson(@Nonnull UserProfileEntity entity) {
        String avatarBase64 = entity.getAvatar() != null
                ? new String(entity.getAvatar())
                : null;

        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getLastname(),
                avatarBase64
        );
    }

    private void updateEntityFromRequest(@Nonnull UserProfileEntity entity, @Nonnull UpdateUserRequest request) {
        entity.setFirstname(request.getFirstname());
        entity.setLastname(request.getLastname());
        entity.setAvatar(request.getAvatar().getBytes(StandardCharsets.UTF_8));
    }

    @KafkaListener(topics = "users", groupId = "userdata")
    @Transactional
    public void createUser(@Nonnull UserJson userJson) {
        log.debug("Received user from Kafka: {}", userJson.username());

        if (userProfileRepository.findByUsername(userJson.username()).isPresent()) {
            log.debug("User already exists: {}", userJson.username());
            return;
        }

        UserProfileEntity entity = new UserProfileEntity();
        entity.setUsername(userJson.username());

        userProfileRepository.save(entity);
        log.info("Created new user from Kafka: {}", userJson.username());
    }
}