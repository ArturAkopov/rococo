package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.data.repository.ArtistRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistDatabaseService {

    private final ArtistRepository artistRepository;

    public ArtistEntity getArtist(@Nonnull UUID id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));
    }

    public Page<ArtistEntity> getAllArtists(@Nonnull PageRequest pageable) {
        return artistRepository.findAll(pageable);
    }

    public Page<ArtistEntity> searchArtistsByName(@Nonnull String name, @Nonnull PageRequest pageable) {
        return artistRepository.findByName(name, pageable);
    }

    public ArtistEntity createArtist(@Nonnull String name, @Nonnull String biography, @Nonnull byte[] photo) {
        ArtistEntity newArtist = new ArtistEntity();
        newArtist.setName(name);
        newArtist.setBiography(biography);
        newArtist.setPhoto(photo);

        return artistRepository.save(newArtist);
    }

    public ArtistEntity updateArtist(UUID id, String name, String biography, @Nullable byte[] photo) {
        ArtistEntity artist = artistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Artist not found with id: " + id));

        artist.setName(name);
        artist.setBiography(biography);
        if (photo != null && photo.length > 0) {
            artist.setPhoto(photo);
        }

        return artistRepository.save(artist);
    }
}
