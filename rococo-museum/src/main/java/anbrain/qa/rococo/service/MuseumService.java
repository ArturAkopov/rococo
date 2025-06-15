package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.data.repository.MuseumRepository;
import anbrain.qa.rococo.exception.NotFoundException;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import jakarta.annotation.Nonnull;
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
public class MuseumService {
    private final MuseumRepository museumRepository;
    private final CountryRepository countryRepository;

    public MuseumJson findById(UUID id) {
        MuseumEntity museum = museumRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Музей не найден с id: " + id));
        return entityToJson(museum);
    }

    public Page<MuseumJson> getAll(Pageable pageable) {
        return museumRepository.findAll(pageable)
                .map(this::entityToJson);
    }

    public List<MuseumJson> searchByTitle(String title) {
        return museumRepository.findByTitleContainsIgnoreCase(title).stream()
                .map(this::entityToJson)
                .toList();
    }

    @Transactional
    public MuseumJson create(@Nonnull MuseumJson museumJson) {
        CountryEntity country = countryRepository.findById(museumJson.geo().country().id())
                .orElseThrow(() -> new NotFoundException("Страна не найдена с id: " + museumJson.geo().country().id()));

        MuseumEntity entity = new MuseumEntity();
        mapMuseumEntity(entity, country, museumJson);

        MuseumEntity saved = museumRepository.save(entity);
        return entityToJson(saved);
    }

    @Transactional
    public MuseumJson update(@Nonnull MuseumJson museum) {
        MuseumEntity entity = museumRepository.findById(museum.id())
                .orElseThrow(() -> new NotFoundException("Музей не найден с id: " + museum.id()));

        CountryEntity country = countryRepository.findById(museum.geo().country().id())
                .orElseThrow(() -> new NotFoundException("Страна не найдена с id: " + museum.geo().country().id()));

        mapMuseumEntity(entity,country,museum);

        MuseumEntity updated = museumRepository.save(entity);
        return entityToJson(updated);
    }

    @Nonnull
    private MuseumJson entityToJson(@Nonnull MuseumEntity entity) {
        return new MuseumJson(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getPhoto() != null ? new String(entity.getPhoto()) : null,
                new GeoJson(
                        entity.getCity(),
                        new CountryJson(
                                entity.getCountry().getId(),
                                entity.getCountry().getName()
                        )
                )
        );
    }

    private void mapMuseumEntity(@Nonnull MuseumEntity entity, @Nonnull CountryEntity country, @Nonnull MuseumJson museum) {
        entity.setTitle(museum.title());
        entity.setDescription(museum.description());
        entity.setCity(museum.geo().city());
        if (museum.photo() != null) {
            entity.setPhoto(museum.photo().getBytes(StandardCharsets.UTF_8));
        }
        entity.setCountry(country);
    }
}