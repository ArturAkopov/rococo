package service;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.exception.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.CountryGrpcClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CountryGrpcClientTests {

    @Mock
    private CountryServiceGrpc.CountryServiceBlockingStub countryStub;

    @InjectMocks
    private CountryGrpcClient countryGrpcClient;

    @Captor
    private ArgumentCaptor<AllCountriesRequest> allCountriesRequestCaptor;

    private final UUID testId = UUID.randomUUID();
    private final String testName = "Test Country";

    @Test
    void getAllCountries_shouldReturnPageOfCountries() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        Country country = Country.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .build();

        AllCountriesResponse response = AllCountriesResponse.newBuilder()
                .addCountries(country)
                .setTotalCount(1)
                .build();

        when(countryStub.getAllCountries(any(AllCountriesRequest.class))).thenReturn(response);

        RestPage<CountryJson> result = countryGrpcClient.getAllCountries(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        CountryJson countryJson = result.getContent().getFirst();
        assertEquals(testId, countryJson.id());
        assertEquals(testName, countryJson.name());

        verify(countryStub).getAllCountries(allCountriesRequestCaptor.capture());
        assertEquals(page, allCountriesRequestCaptor.getValue().getPage());
        assertEquals(size, allCountriesRequestCaptor.getValue().getSize());
    }

    @Test
    void getAllCountries_shouldThrowServiceUnavailableExceptionWhenServiceUnavailable() {
        Pageable pageable = PageRequest.of(0, 10);

        when(countryStub.getAllCountries(any(AllCountriesRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> countryGrpcClient.getAllCountries(pageable));
        assertEquals("Сервис временно недоступен", ex.getMessage());
    }

    @Test
    void getAllCountries_shouldThrowTimeoutExceptionWhenDeadlineExceeded() {
        Pageable pageable = PageRequest.of(0, 10);

        when(countryStub.getAllCountries(any(AllCountriesRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> countryGrpcClient.getAllCountries(pageable));
        assertEquals("Превышено время ожидания ответа от сервиса", ex.getMessage());
    }

    @Test
    void getAllCountries_shouldThrowValidationExceptionWhenInvalidArgument() {
        Pageable pageable = PageRequest.of(0, 10);

        when(countryStub.getAllCountries(any(AllCountriesRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        RococoValidationException ex = assertThrows(RococoValidationException.class,
                () -> countryGrpcClient.getAllCountries(pageable));
        assertEquals("Ошибка валидации данных: Страны - страница 0", ex.getMessage());
    }

    @Test
    void getAllCountries_shouldThrowAccessDeniedExceptionWhenPermissionDenied() {
        Pageable pageable = PageRequest.of(0, 10);

        when(countryStub.getAllCountries(any(AllCountriesRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

        RococoAccessDeniedException ex = assertThrows(RococoAccessDeniedException.class,
                () -> countryGrpcClient.getAllCountries(pageable));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void getAllCountries_shouldThrowRuntimeExceptionWhenUnknownError() {
        Pageable pageable = PageRequest.of(0, 10);

        when(countryStub.getAllCountries(any(AllCountriesRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNKNOWN));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> countryGrpcClient.getAllCountries(pageable));
        assertTrue(ex.getMessage().contains("Ошибка при обработке Страны: страница 0"));
    }

    @Test
    void toCountryJson_shouldConvertCountryToJson() {
        Country country = Country.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .build();

        CountryJson result = countryGrpcClient.toCountryJson(country);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testName, result.name());
    }
}