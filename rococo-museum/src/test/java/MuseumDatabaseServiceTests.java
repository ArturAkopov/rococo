import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.data.repository.MuseumRepository;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.service.MuseumDatabaseService;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseumDatabaseServiceTests {

    private UUID museumId;
    private UUID countryId;
    CountryEntity countryEntity;

    @BeforeEach
    void setUp() {
        museumId = UUID.randomUUID();
        countryId = UUID.randomUUID();
        countryEntity = new CountryEntity();
        countryEntity.setId(countryId);
        countryEntity.setName("Country");
    }

    @Mock
    private MuseumRepository museumRepository;

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private MuseumDatabaseService museumDatabaseService;

    @Test
    void findById_shouldReturnMuseumWhenExists() {
        MuseumEntity expected = new MuseumEntity();

        expected.setId(museumId);

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(expected));

        MuseumEntity result = museumDatabaseService.findById(museumId);

        assertNotNull(result);
        assertEquals(museumId, result.getId());
        verify(museumRepository, times(1)).findById(museumId);
    }

    @Test
    void findById_shouldThrowExceptionWhenNotExists() {
        when(museumRepository.findById(museumId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> museumDatabaseService.findById(museumId));

        assertEquals("Музей не найден с id: " + museumId, exception.getMessage());
        verify(museumRepository, times(1)).findById(museumId);
    }

    @Test
    void getAll_shouldReturnPageOfMuseums() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<MuseumEntity> museums = List.of(new MuseumEntity(), new MuseumEntity());
        Page<MuseumEntity> expectedPage = new PageImpl<>(museums);

        when(museumRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<MuseumEntity> result = museumDatabaseService.getAll(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(museumRepository, times(1)).findAll(pageable);
    }

    @Test
    void searchByTitle_shouldReturnMatchingMuseums() {
        String searchTitle = "test";
        List<MuseumEntity> expected = List.of(new MuseumEntity(), new MuseumEntity());

        when(museumRepository.findByTitleContainsIgnoreCase(searchTitle)).thenReturn(expected);

        List<MuseumEntity> result = museumDatabaseService.searchByTitle(searchTitle);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(museumRepository, times(1)).findByTitleContainsIgnoreCase(searchTitle);
    }

    @Test
    void create_shouldSaveNewMuseum() {
        MuseumJson museumJson = new MuseumJson(
                null,
                "Test Museum",
                "Test Description",
                "photo",
                new GeoJson("City", new CountryJson(countryId, null))
        );

        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
        when(museumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDatabaseService.create(museumJson);

        assertNotNull(result);
        assertEquals("Test Museum", result.getTitle());
        assertEquals(countryId, result.getCountry().getId());
        verify(countryRepository, times(1)).findById(countryId);
        verify(museumRepository, times(1)).save(any());
    }

    @Test
    void update_shouldUpdateExistingMuseum() {
        MuseumEntity existing = new MuseumEntity();
        existing.setId(museumId);

        MuseumJson museumJson = new MuseumJson(
                museumId,
                "Updated Museum",
                "Updated Description",
                "new photo",
                new GeoJson("Updated City", new CountryJson(countryId, "Country"))
        );

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existing));
        when(countryRepository.findById(countryId)).thenReturn(Optional.of(countryEntity));
        when(museumRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MuseumEntity result = museumDatabaseService.update(museumJson);

        assertNotNull(result);
        assertEquals("Updated Museum", result.getTitle());
        assertEquals(countryId, result.getCountry().getId());
        verify(museumRepository, times(1)).findById(museumId);
        verify(countryRepository, times(1)).findById(countryId);
        verify(museumRepository, times(1)).save(any());
    }

    @Test
    void create_shouldThrowExceptionWhenCountryNotFound() {
        MuseumJson museumJson = new MuseumJson(
                null,
                "Test Museum",
                "Test Description",
                "photo",
                new GeoJson("City", new CountryJson(countryId, null))
        );

        when(countryRepository.findById(countryId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> museumDatabaseService.create(museumJson));

        assertEquals("Страна не найдена с id: " + countryId, exception.getMessage());
        verify(countryRepository, times(1)).findById(countryId);
        verify(museumRepository, never()).save(any());
    }

    @Test
    void update_shouldThrowExceptionWhenCountryNotFound() {

        MuseumEntity existing = new MuseumEntity();
        existing.setId(museumId);

        MuseumJson museumJson = new MuseumJson(
                museumId,
                "Updated Museum",
                "Updated Description",
                "new photo",
                new GeoJson("Updated City", new CountryJson(countryId, "Country"))
        );

        when(museumRepository.findById(museumId)).thenReturn(Optional.of(existing));
        when(countryRepository.findById(countryId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> museumDatabaseService.update(museumJson));

        assertEquals("Страна не найдена с id: " + countryId, exception.getMessage());
        verify(museumRepository, times(1)).findById(museumId);
        verify(countryRepository, times(1)).findById(countryId);
        verify(museumRepository, never()).save(any());
    }
}