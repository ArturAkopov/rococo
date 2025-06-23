import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.data.repository.PaintingRepository;
import anbrain.qa.rococo.service.PaintingDatabaseService;
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
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaintingDatabaseServiceTests {

    @Mock
    private PaintingRepository paintingRepository;

    @InjectMocks
    private PaintingDatabaseService paintingDatabaseService;

    private final UUID testId = UUID.randomUUID();
    private final UUID artistId = UUID.randomUUID();
    private final UUID museumId = UUID.randomUUID();
    private PaintingEntity testPainting;

    @BeforeEach
    void setUp() {
        testPainting = new PaintingEntity();
        testPainting.setId(testId);
        testPainting.setTitle("Test Painting");
        testPainting.setDescription("Test Description");
        testPainting.setContent("test content".getBytes());
        testPainting.setArtistId(artistId);
        testPainting.setMuseumId(museumId);
    }

    @Test
    void shouldSuccessfullyGetPaintingById() {
        when(paintingRepository.findById(testId)).thenReturn(Optional.of(testPainting));

        PaintingEntity result = paintingDatabaseService.getPainting(testId);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(paintingRepository, times(1)).findById(testId);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenPaintingNotFound() {
        when(paintingRepository.findById(testId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> paintingDatabaseService.getPainting(testId));

        assertEquals("Картина не найдена с id: " + testId, exception.getMessage());
        verify(paintingRepository, times(1)).findById(testId);
    }

    @Test
    void shouldReturnAllPaintingsWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> expectedPage = new PageImpl<>(Collections.singletonList(testPainting));

        when(paintingRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<PaintingEntity> result = paintingDatabaseService.getAllPaintings(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paintingRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldReturnPaintingsByArtistWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> expectedPage = new PageImpl<>(Collections.singletonList(testPainting));

        when(paintingRepository.findByArtistId(artistId, pageable)).thenReturn(expectedPage);

        Page<PaintingEntity> result = paintingDatabaseService.getPaintingsByArtist(artistId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paintingRepository, times(1)).findByArtistId(artistId, pageable);
    }

    @Test
    void shouldReturnPaintingsByMuseumWithPagination() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> expectedPage = new PageImpl<>(Collections.singletonList(testPainting));

        when(paintingRepository.findByMuseumId(museumId, pageable)).thenReturn(expectedPage);

        Page<PaintingEntity> result = paintingDatabaseService.getPaintingsByMuseum(museumId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paintingRepository, times(1)).findByMuseumId(museumId, pageable);
    }

    @Test
    void shouldReturnPaintingsByTitle() {
        String title = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Page<PaintingEntity> expectedPage = new PageImpl<>(Collections.singletonList(testPainting));

        when(paintingRepository.findByTitleContainingIgnoreCase(title, pageable)).thenReturn(expectedPage);

        Page<PaintingEntity> result = paintingDatabaseService.getPaintingsByTitle(title, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(paintingRepository, times(1)).findByTitleContainingIgnoreCase(title, pageable);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenTitleIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paintingDatabaseService.getPaintingsByTitle(" ", pageable));

        assertEquals("Название для поиска не может быть пустым", exception.getMessage());
        verify(paintingRepository, never()).findByTitleContainingIgnoreCase(any(), any());
    }

    @Test
    void shouldSuccessfullyCreatePainting() {
        when(paintingRepository.save(testPainting)).thenReturn(testPainting);

        PaintingEntity result = paintingDatabaseService.createPainting(testPainting);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(paintingRepository, times(1)).save(testPainting);
    }

    @Test
    void shouldSuccessfullyUpdatePainting() {
        when(paintingRepository.save(testPainting)).thenReturn(testPainting);

        PaintingEntity result = paintingDatabaseService.updatePainting(testPainting);

        assertNotNull(result);
        assertEquals(testId, result.getId());
        verify(paintingRepository, times(1)).save(testPainting);
    }
}