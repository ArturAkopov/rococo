package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.UserProfileEntity;
import anbrain.qa.rococo.data.repository.UserProfileRepository;
import anbrain.qa.rococo.model.UserJson;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserdataDatabaseService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileEntity getUserByUsername(String username) {
        log.debug("Поиск пользователя в БД: {}", username);
        return userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + username));
    }

    public UserProfileEntity updateUser(String username, String firstname, String lastname, String avatar) {
        log.debug("Обновление данных пользователя: {}", username);

        UserProfileEntity entity = userProfileRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден: " + username));

        updateEntity(entity, firstname, lastname, avatar);

        UserProfileEntity saved = userProfileRepository.save(entity);
        log.info("Данные пользователя {} успешно обновлены", username);
        return saved;
    }

    @KafkaListener(topics = "users", groupId = "userdata")
    @Transactional
    public void createUser(@Nonnull UserJson userJson) {
        log.debug("Получение пользователя из Kafka: {}", userJson.username());

        if (userProfileRepository.findByUsername(userJson.username()).isPresent()) {
            log.debug("Пользователь {} уже существует", userJson.username());
            return;
        }

        UserProfileEntity entity = new UserProfileEntity();
        entity.setUsername(userJson.username());

        userProfileRepository.save(entity);
        log.info("Создан новый пользователь из Kafka: {}", userJson.username());
    }

    private void updateEntity(@Nonnull UserProfileEntity entity, String firstname, String lastname, String avatar) {
        entity.setFirstname(firstname);
        entity.setLastname(lastname);
        if (avatar != null && !avatar.isBlank()) {
            entity.setAvatar(avatar.getBytes());
        }
    }
}