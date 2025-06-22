package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.data.repository.MuseumRepository;
import anbrain.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MuseumDatabaseService {

    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumEntity findById(UUID id) {
        log.debug("Поиск музея по ID: {}", id);
        return museumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Музей не найден с id: " + id));
    }

    public Page<MuseumEntity> getAll(Pageable pageable) {
        log.debug("Получение всех музеев. Страница: {}, Размер: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        return museumRepository.findAll(pageable);
    }

    public List<MuseumEntity> searchByTitle(String title) {
        log.debug("Поиск музеев по названию: '{}'", title);
        return museumRepository.findByTitleContainsIgnoreCase(title);
    }

    @Transactional
    public MuseumEntity create(@Nonnull MuseumJson museumJson) {
        log.debug("Создание нового музея: '{}'", museumJson.title());

        CountryEntity country = getCountryEntity(museumJson.geo().country().id());

        MuseumEntity entity = new MuseumEntity();
        mapToEntity(entity, country, museumJson);

        MuseumEntity saved = museumRepository.save(entity);
        log.info("Музей '{}' успешно создан с ID: {}", saved.getTitle(), saved.getId());
        return saved;
    }

    @Transactional
    public MuseumEntity update(@Nonnull MuseumJson museumJson) {
        log.debug("Обновление музея с ID: {}", museumJson.id());

        MuseumEntity entity = getMuseumEntity(museumJson.id());
        CountryEntity country = getCountryEntity(museumJson.geo().country().id());

        mapToEntity(entity, country, museumJson);

        MuseumEntity updated = museumRepository.save(entity);
        log.info("Музей с ID {} успешно обновлен", updated.getId());
        return updated;
    }

    private CountryEntity getCountryEntity(UUID countryId) {
        log.debug("Получение страны с ID: {}", countryId);
        return countryRepository.findById(countryId)
                .orElseThrow(() -> new EntityNotFoundException("Страна не найдена с id: " + countryId));
    }

    private MuseumEntity getMuseumEntity(UUID museumId) {
        log.debug("Получение музея с ID: {}", museumId);
        return museumRepository.findById(museumId)
                .orElseThrow(() -> new EntityNotFoundException("Музей не найден с id: " + museumId));
    }

    private void mapToEntity(@Nonnull MuseumEntity entity, CountryEntity country, @Nonnull MuseumJson museum) {
        entity.setTitle(museum.title());
        entity.setDescription(museum.description());
        entity.setCity(museum.geo().city());
        if (museum.photo() != null) {
            entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        entity.setCountry(country);
    }
}