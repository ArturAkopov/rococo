package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.data.repository.MuseumRepository;
import anbrain.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MuseumDatabaseService {
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumEntity findById(UUID id) {
        return museumRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Музей не найден с id: " + id));
    }

    public Page<MuseumEntity> getAll(Pageable pageable) {
        return museumRepository.findAll(pageable);
    }

    public List<MuseumEntity> searchByTitle(String title) {
        return museumRepository.findByTitleContainsIgnoreCase(title);
    }

    @Transactional
    public MuseumEntity create(@Nonnull MuseumJson museumJson) {
        CountryEntity country = countryRepository.findById(museumJson.geo().country().id())
                .orElseThrow(() -> new EntityNotFoundException("Страна не найдена с id: " + museumJson.geo().country().id()));

        MuseumEntity entity = new MuseumEntity();
        mapMuseumEntity(entity, country, museumJson);

        return museumRepository.save(entity);
    }

    @Transactional
    public MuseumEntity update(@Nonnull MuseumJson museumJson) {
        MuseumEntity entity = museumRepository.findById(museumJson.id())
                .orElseThrow(() -> new EntityNotFoundException("Музей не найден с id: " + museumJson.id()));

        CountryEntity country = countryRepository.findById(museumJson.geo().country().id())
                .orElseThrow(() -> new EntityNotFoundException("Страна не найдена с id: " + museumJson.geo().country().id()));

        mapMuseumEntity(entity, country, museumJson);

        return museumRepository.save(entity);
    }

    private void mapMuseumEntity(@Nullable MuseumEntity existingEntity, @Nonnull CountryEntity country, @Nonnull MuseumJson museum) {
        MuseumEntity entity = existingEntity != null ? existingEntity : new MuseumEntity();
        entity.setTitle(museum.title());
        entity.setDescription(museum.description());
        entity.setCity(museum.geo().city());
        if (museum.photo() != null) {
            entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        entity.setCountry(country);
    }
}