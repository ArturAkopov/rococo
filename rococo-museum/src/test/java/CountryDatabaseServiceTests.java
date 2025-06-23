import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.service.CountryDatabaseService;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryDatabaseServiceTests {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryDatabaseService countryDatabaseService;

    private CountryEntity entity1;
    private CountryEntity entity2;

    @BeforeEach
    void setUp() {

        entity1 = new CountryEntity();
        entity1.setId(UUID.randomUUID());
        entity1.setName("Country 1");

        entity2 = new CountryEntity();
        entity2.setId(UUID.randomUUID());
        entity2.setName("Country 2");

    }

    @Test
    void getAllCountries_shouldReturnPageOfCountries() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<CountryEntity> countries = List.of(
                entity1,
                entity2
        );
        Page<CountryEntity> expectedPage = new PageImpl<>(countries);

        when(countryRepository.findAll(pageable)).thenReturn(expectedPage);

        Page<CountryEntity> result = countryDatabaseService.getAllCountries(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(countryRepository, times(1)).findAll(pageable);
    }
}