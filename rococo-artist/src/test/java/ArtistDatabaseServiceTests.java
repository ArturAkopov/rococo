import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.data.repository.ArtistRepository;
import anbrain.qa.rococo.service.ArtistDatabaseService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistDatabaseServiceTests {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistDatabaseService artistDatabaseService;

    private UUID existingArtistId;
    private UUID nonExistingArtistId;
    private ArtistEntity testArtist;
    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        existingArtistId = UUID.randomUUID();
        nonExistingArtistId = UUID.randomUUID();
        pageRequest = PageRequest.of(0, 10);

        testArtist = new ArtistEntity();
        testArtist.setId(existingArtistId);
        testArtist.setName("Test Artist");
        testArtist.setBiography("Test Biography");
        testArtist.setPhoto("photo".getBytes());
    }

    @Test
    void getArtist_shouldReturnArtistWhenExists() {
        when(artistRepository.findById(existingArtistId)).thenReturn(Optional.of(testArtist));

        ArtistEntity result = artistDatabaseService.getArtist(existingArtistId);

        assertNotNull(result);
        assertEquals(existingArtistId, result.getId());
        verify(artistRepository, times(1)).findById(existingArtistId);
    }

    @Test
    void getArtist_shouldThrowExceptionWhenNotExists() {
        when(artistRepository.findById(nonExistingArtistId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                artistDatabaseService.getArtist(nonExistingArtistId)
        );

        assertEquals("Не найден Артист с id: " + nonExistingArtistId, exception.getMessage());
        verify(artistRepository, times(1)).findById(nonExistingArtistId);
    }

    @Test
    void getAllArtists_shouldReturnPageOfArtists() {
        Page<ArtistEntity> expectedPage = new PageImpl<>(List.of(testArtist));
        when(artistRepository.findAll(pageRequest)).thenReturn(expectedPage);

        Page<ArtistEntity> result = artistDatabaseService.getAllArtists(pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(artistRepository, times(1)).findAll(pageRequest);
    }

    @Test
    void searchArtistsByName_shouldReturnFilteredPage() {
        String searchName = "test";
        Page<ArtistEntity> expectedPage = new PageImpl<>(List.of(testArtist));
        when(artistRepository.findByNameContainsIgnoreCase(searchName, pageRequest)).thenReturn(expectedPage);

        Page<ArtistEntity> result = artistDatabaseService.searchArtistsByName(searchName, pageRequest);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(artistRepository, times(1)).findByNameContainsIgnoreCase(searchName, pageRequest);
    }

    @Test
    void createArtist_shouldSaveNewArtist() {
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(testArtist);

        ArtistEntity result = artistDatabaseService.createArtist(
                testArtist.getName(),
                testArtist.getBiography(),
                testArtist.getPhoto()
        );

        assertNotNull(result);
        assertEquals(testArtist.getName(), result.getName());
        verify(artistRepository, times(1)).save(any(ArtistEntity.class));
    }

    @Test
    void updateArtist_shouldUpdateExistingArtist() {
        ArtistEntity updatedArtist = new ArtistEntity();
        updatedArtist.setId(existingArtistId);
        updatedArtist.setName("Updated Name");
        updatedArtist.setBiography("Updated Bio");
        updatedArtist.setPhoto("new photo".getBytes());

        when(artistRepository.findById(existingArtistId)).thenReturn(Optional.of(testArtist));
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(updatedArtist);

        ArtistEntity result = artistDatabaseService.updateArtist(
                existingArtistId,
                updatedArtist.getName(),
                updatedArtist.getBiography(),
                updatedArtist.getPhoto()
        );

        assertNotNull(result);
        assertEquals(updatedArtist.getName(), result.getName());
        assertEquals(updatedArtist.getBiography(), result.getBiography());
        verify(artistRepository, times(1)).findById(existingArtistId);
        verify(artistRepository, times(1)).save(any(ArtistEntity.class));
    }

    @Test
    void updateArtist_shouldNotUpdatePhotoWhenNull() {
        ArtistEntity updatedArtist = new ArtistEntity();
        updatedArtist.setId(existingArtistId);
        updatedArtist.setName("Updated Name");
        updatedArtist.setBiography("Updated Bio");
        updatedArtist.setPhoto(testArtist.getPhoto());

        when(artistRepository.findById(existingArtistId)).thenReturn(Optional.of(testArtist));
        when(artistRepository.save(any(ArtistEntity.class))).thenReturn(updatedArtist);

        ArtistEntity result = artistDatabaseService.updateArtist(
                existingArtistId,
                updatedArtist.getName(),
                updatedArtist.getBiography(),
                null
        );

        assertNotNull(result);
        assertArrayEquals(testArtist.getPhoto(), result.getPhoto());
        verify(artistRepository, times(1)).findById(existingArtistId);
        verify(artistRepository, times(1)).save(any(ArtistEntity.class));
    }

    @Test
    void updateArtist_shouldThrowExceptionWhenArtistNotFound() {
        when(artistRepository.findById(nonExistingArtistId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                artistDatabaseService.updateArtist(
                        nonExistingArtistId,
                        "Name",
                        "Bio",
                        "photo".getBytes()
                )
        );
        assertEquals("Не найден Артист с id: " + nonExistingArtistId, exception.getMessage());
        verify(artistRepository, times(1)).findById(nonExistingArtistId);
        verify(artistRepository, never()).save(any());
    }
}