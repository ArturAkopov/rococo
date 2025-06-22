package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.data.repository.PaintingRepository;
import jakarta.annotation.Nonnull;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaintingDatabaseService {

    private final PaintingRepository paintingRepository;

    public PaintingEntity getPainting(UUID id) {
        log.debug("Поиск картины по ID: {}", id);
        return paintingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Картина не найдена с id: " + id));
    }

    public Page<PaintingEntity> getAllPaintings(@Nonnull Pageable pageable) {
        log.debug("Получение всех картин. Страница: {}, Размер: {}", pageable.getPageNumber(), pageable.getPageSize());
        return paintingRepository.findAll(pageable);
    }

    public Page<PaintingEntity> getPaintingsByArtist(UUID artistId, Pageable pageable) {
        log.debug("Поиск картин художника с ID: {}", artistId);
        return paintingRepository.findByArtistId(artistId, pageable);
    }

    public Page<PaintingEntity> getPaintingsByMuseum(UUID museumId, Pageable pageable) {
        log.debug("Поиск картин музея с ID: {}", museumId);
        return paintingRepository.findByMuseumId(museumId, pageable);
    }

    public Page<PaintingEntity> getPaintingsByTitle(String title, Pageable pageable) {
        log.debug("Поиск картин по названию: '{}'", title);
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название для поиска не может быть пустым");
        }
        return paintingRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    @Transactional
    public PaintingEntity createPainting(@Nonnull PaintingEntity painting) {
        log.debug("Создание новой картины: '{}'", painting.getTitle());
        PaintingEntity saved = paintingRepository.save(painting);
        log.info("Картина '{}' успешно создана с ID: {}", saved.getTitle(), saved.getId());
        return saved;
    }

    @Transactional
    public PaintingEntity updatePainting(@Nonnull PaintingEntity painting) {
        log.debug("Обновление картины с ID: {}", painting.getId());
        PaintingEntity updated = paintingRepository.save(painting);
        log.info("Картина с ID {} успешно обновлена", updated.getId());
        return updated;
    }
}