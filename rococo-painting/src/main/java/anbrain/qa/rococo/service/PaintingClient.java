package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.data.repository.PaintingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaintingClient {

    private final PaintingRepository paintingRepository;

    public PaintingClient(PaintingRepository paintingRepository) {
        this.paintingRepository = paintingRepository;
    }

    public PaintingEntity getPainting(UUID id) {
        return paintingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Painting not found"));
    }

    public Page<PaintingEntity> getAllPaintings(Pageable pageable) {
        return paintingRepository.findAll(pageable);
    }

    public Page<PaintingEntity> getPaintingsByArtist(UUID artistId, Pageable pageable) {
        return paintingRepository.findByArtistId(artistId, pageable);
    }

    public Page<PaintingEntity> getPaintingsByMuseum(UUID museumId, Pageable pageable) {
        return paintingRepository.findByMuseumId(museumId, pageable);
    }

    public Page<PaintingEntity> getPaintingsByTitle(String title, Pageable pageable) {
        return paintingRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public PaintingEntity createPainting(PaintingEntity painting) {
        return paintingRepository.save(painting);
    }

    public PaintingEntity updatePainting(PaintingEntity painting) {
        return paintingRepository.save(painting);
    }
}