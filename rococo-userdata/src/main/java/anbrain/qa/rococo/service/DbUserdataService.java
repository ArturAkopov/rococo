package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.data.repository.UserProfileRepository;
import anbrain.qa.rococo.exception.UserNotFoundException;
import anbrain.qa.rococo.grpc.UpdateUserRequest;
import anbrain.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbUserdataService implements UserdataService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserJson getUser(String username) {
        log.debug("Getting user by username: {}", username);
        return userProfileRepository.findByUsername(username)
                .map(this::toUserJson)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Override
    @Transactional
    public UserJson updateUser(@Nonnull UpdateUserRequest userRequest) {
        log.debug("Updating user: {}", userRequest.getUsername());
        UserProfileEntity entity = userProfileRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userRequest.getUsername()));

        updateEntityFromRequest(entity, userRequest);
        return toUserJson(userProfileRepository.save(entity));
    }

    private void updateEntityFromRequest(@Nonnull UserProfileEntity entity, @Nonnull UpdateUserRequest request) {
        entity.setFirstname(request.getFirstname());
        entity.setLastname(request.getLastname());
        entity.setAvatar(request.getAvatar().getBytes(StandardCharsets.UTF_8));
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

    @KafkaListener(topics = "users", groupId = "userdata")
    @Transactional
    public void createUser(@Nonnull UserJson userJson) {
        log.debug("Received user from Kafka: {}", userJson.username());

        if (userProfileRepository.findByUsername (userJson.username()).isPresent()) {
            log.debug("User already exists: {}", userJson.username());
            return;
        }

        UserProfileEntity entity = new UserProfileEntity();
        entity.setUsername(userJson.username());

        userProfileRepository.save(entity);
        log.info("Created new user from Kafka: {}", userJson.username());
    }
}