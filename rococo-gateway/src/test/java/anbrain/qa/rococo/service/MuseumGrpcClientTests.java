package anbrain.qa.rococo.service;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.exception.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.GeoJson;
import anbrain.qa.rococo.model.MuseumJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.MuseumGrpcClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MuseumGrpcClientTests {

    @Mock
    private MuseumServiceGrpc.MuseumServiceBlockingStub museumStub;

    @InjectMocks
    private MuseumGrpcClient museumGrpcClient;

    @Captor
    private ArgumentCaptor<MuseumRequest> museumRequestCaptor;
    @Captor
    private ArgumentCaptor<AllMuseumsRequest> allMuseumsRequestCaptor;
    @Captor
    private ArgumentCaptor<SearchMuseumsRequest> searchMuseumsRequestCaptor;
    @Captor
    private ArgumentCaptor<CreateMuseumRequest> createMuseumRequestCaptor;
    @Captor
    private ArgumentCaptor<UpdateMuseumRequest> updateMuseumRequestCaptor;

    private final UUID testId = UUID.randomUUID();
    private final String testTitle = "Test Museum";
    private final String testDescription = "Test Description";
    private final String testPhoto = "photo1";
    private final String testCity = "Test City";
    private final UUID testCountryId = UUID.randomUUID();
    private final String testCountryName = "Test Country";

    @Test
    void getMuseum_shouldReturnMuseumWhenFound() {
        MuseumResponse response = createTestMuseumResponse();

        when(museumStub.getMuseum(any(MuseumRequest.class))).thenReturn(response);

        MuseumJson result = museumGrpcClient.getMuseum(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());
        assertEquals(testDescription, result.description());
        assertEquals(testPhoto, result.photo());
        assertEquals(testCity, result.geo().city());
        assertEquals(testCountryId, result.geo().country().id());
        assertEquals(testCountryName, result.geo().country().name());

        verify(museumStub).getMuseum(museumRequestCaptor.capture());
        assertEquals(testId.toString(), museumRequestCaptor.getValue().getId());
    }

    @Test
    void getAllMuseums_shouldReturnPageOfMuseums() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        AllMuseumsResponse response = AllMuseumsResponse.newBuilder()
                .addMuseums(createTestMuseumResponse())
                .setTotalCount(1)
                .build();

        when(museumStub.getAllMuseums(any(AllMuseumsRequest.class))).thenReturn(response);

        RestPage<MuseumJson> result = museumGrpcClient.getAllMuseums(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        MuseumJson museumJson = result.getContent().getFirst();
        assertEquals(testId, museumJson.id());
        assertEquals(testTitle, museumJson.title());

        verify(museumStub).getAllMuseums(allMuseumsRequestCaptor.capture());
        assertEquals(page, allMuseumsRequestCaptor.getValue().getPage());
        assertEquals(size, allMuseumsRequestCaptor.getValue().getSize());
    }

    @Test
    void searchMuseumsByTitle_shouldReturnFilteredMuseums() {
        String searchTitle = "search";

        SearchMuseumsResponse response = SearchMuseumsResponse.newBuilder()
                .addMuseums(createTestMuseumResponse())
                .build();

        when(museumStub.searchMuseumsByTitle(any(SearchMuseumsRequest.class))).thenReturn(response);

        List<MuseumJson> result = museumGrpcClient.searchMuseumsByTitle(searchTitle);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(museumStub).searchMuseumsByTitle(searchMuseumsRequestCaptor.capture());
        assertEquals(searchTitle, searchMuseumsRequestCaptor.getValue().getTitle());
    }

    @Test
    void createMuseum_shouldReturnCreatedMuseum() {
        MuseumJson newMuseum = new MuseumJson(
                null,
                testTitle,
                testDescription,
                testPhoto,
                new GeoJson(testCity, new CountryJson(testCountryId, testCountryName))
        );

        MuseumResponse response = createTestMuseumResponse();

        when(museumStub.createMuseum(any(CreateMuseumRequest.class))).thenReturn(response);

        MuseumJson result = museumGrpcClient.createMuseum(newMuseum);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());

        verify(museumStub).createMuseum(createMuseumRequestCaptor.capture());
        assertEquals(testTitle, createMuseumRequestCaptor.getValue().getTitle());
        assertEquals(testDescription, createMuseumRequestCaptor.getValue().getDescription());
        assertEquals(testPhoto, createMuseumRequestCaptor.getValue().getPhoto());
        assertEquals(testCity, createMuseumRequestCaptor.getValue().getGeo().getCity());
        assertEquals(testCountryId.toString(), createMuseumRequestCaptor.getValue().getGeo().getCountry().getId());
    }

    @Test
    void updateMuseum_shouldReturnUpdatedMuseum() {
        MuseumJson updatedMuseum = new MuseumJson(
                testId,
                testTitle,
                testDescription,
                testPhoto,
                new GeoJson(testCity, new CountryJson(testCountryId, testCountryName))
        );

        MuseumResponse response = createTestMuseumResponse();

        when(museumStub.updateMuseum(any(UpdateMuseumRequest.class))).thenReturn(response);

        MuseumJson result = museumGrpcClient.updateMuseum(updatedMuseum);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());

        verify(museumStub).updateMuseum(updateMuseumRequestCaptor.capture());
        assertEquals(testId.toString(), updateMuseumRequestCaptor.getValue().getId());
        assertEquals(testTitle, updateMuseumRequestCaptor.getValue().getTitle());
    }

    @Test
    void getMuseum_shouldThrowNotFoundExceptionWhenNotFound() {
        when(museumStub.getMuseum(any(MuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        RococoNotFoundException ex = assertThrows(RococoNotFoundException.class,
                () -> museumGrpcClient.getMuseum(testId));
        assertEquals("Музей с ID " + testId + " не найден", ex.getMessage());
    }

    @Test
    void getMuseum_shouldThrowServiceUnavailableExceptionWhenServiceUnavailable() {
        when(museumStub.getMuseum(any(MuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> museumGrpcClient.getMuseum(testId));
        assertEquals("Сервис временно недоступен", ex.getMessage());
    }

    @Test
    void getAllMuseums_shouldThrowValidationExceptionWhenInvalidArgument() {
        Pageable pageable = PageRequest.of(0, 10);

        when(museumStub.getAllMuseums(any(AllMuseumsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        RococoValidationException ex = assertThrows(RococoValidationException.class,
                () -> museumGrpcClient.getAllMuseums(pageable));
        assertEquals("Ошибка валидации данных: Музеи - страница 0", ex.getMessage());
    }

    @Test
    void searchMuseumsByTitle_shouldThrowAccessDeniedExceptionWhenPermissionDenied() {
        when(museumStub.searchMuseumsByTitle(any(SearchMuseumsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

        RococoAccessDeniedException ex = assertThrows(RococoAccessDeniedException.class,
                () -> museumGrpcClient.searchMuseumsByTitle(testTitle));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void createMuseum_shouldThrowConflictExceptionWhenAlreadyExists() {
        MuseumJson newMuseum = new MuseumJson(
                null,
                testTitle,
                testDescription,
                testPhoto,
                new GeoJson(testCity, new CountryJson(testCountryId, testCountryName))
        );

        when(museumStub.createMuseum(any(CreateMuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.ALREADY_EXISTS));

        RococoConflictException ex = assertThrows(RococoConflictException.class,
                () -> museumGrpcClient.createMuseum(newMuseum));
        assertEquals("Создание музея с такими параметрами уже существует: Test Museum", ex.getMessage());
    }

    @Test
    void updateMuseum_shouldThrowTimeoutExceptionWhenDeadlineExceeded() {
        MuseumJson updatedMuseum = new MuseumJson(
                testId,
                testTitle,
                testDescription,
                testPhoto,
                new GeoJson(testCity, new CountryJson(testCountryId, testCountryName))
        );

        when(museumStub.updateMuseum(any(UpdateMuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> museumGrpcClient.updateMuseum(updatedMuseum));
        assertEquals("Превышено время ожидания ответа от сервиса", ex.getMessage());
    }

    @Test
    void updateMuseum_shouldThrowRuntimeExceptionWhenUnknownError() {
        MuseumJson updatedMuseum = new MuseumJson(
                testId,
                testTitle,
                testDescription,
                testPhoto,
                new GeoJson(testCity, new CountryJson(testCountryId, testCountryName))
        );

        when(museumStub.updateMuseum(any(UpdateMuseumRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNKNOWN));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> museumGrpcClient.updateMuseum(updatedMuseum));
        assertTrue(ex.getMessage().contains("Ошибка при обработке Обновление музея: " + testId));
    }

    @Test
    void toGrpcGeo_shouldConvertGeoJsonToGrpcGeo() {
        GeoJson geoJson = new GeoJson(testCity, new CountryJson(testCountryId, testCountryName));
        Geo result = museumGrpcClient.toGrpcGeo(geoJson);

        assertNotNull(result);
        assertEquals(testCity, result.getCity());
        assertEquals(testCountryId.toString(), result.getCountry().getId());
        assertEquals(testCountryName, result.getCountry().getName());
    }

    @Test
    void toMuseumJson_shouldConvertResponseToMuseumJson() {
        MuseumResponse response = createTestMuseumResponse();
        MuseumJson result = museumGrpcClient.toMuseumJson(response);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());
        assertEquals(testDescription, result.description());
        assertEquals(testPhoto, result.photo());
        assertEquals(testCity, result.geo().city());
        assertEquals(testCountryId, result.geo().country().id());
        assertEquals(testCountryName, result.geo().country().name());
    }

    @Nonnull
    private MuseumResponse createTestMuseumResponse() {
        return MuseumResponse.newBuilder()
                .setId(testId.toString())
                .setTitle(testTitle)
                .setDescription(testDescription)
                .setPhoto(testPhoto)
                .setGeo(Geo.newBuilder()
                        .setCity(testCity)
                        .setCountry(Country.newBuilder()
                                .setId(testCountryId.toString())
                                .setName(testCountryName)
                                .build())
                        .build())
                .build();
    }
}