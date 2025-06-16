package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.data.repository.UserProfileRepository;
import anbrain.qa.rococo.exception.UserNotFoundException;
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
public class UserdataDatabaseService {

    private final UserProfileRepository userProfileRepository;

    @Transactional(readOnly = true)
    public UserJson getUserByUsername(String username) {
        return userProfileRepository.findByUsername(username)
                .map(this::toUserJson)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserJson updateUser(String username, String firstname, String lastname, String avatar) {
        UserProfileEntity entity = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
        updateEntity(entity, firstname, lastname, avatar);
        return toUserJson(userProfileRepository.save(entity));
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

    private void updateEntity(@Nonnull UserProfileEntity entity, String firstname, String lastname, String avatar) {
        entity.setFirstname(firstname);
        entity.setLastname(lastname);
        if (avatar != null) {
            entity.setAvatar(avatar.getBytes(StandardCharsets.UTF_8));
        }
    }
}
