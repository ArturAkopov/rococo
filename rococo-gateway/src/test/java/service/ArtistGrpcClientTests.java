package service;

import anbrain.qa.rococo.exception.*;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.ArtistJson;
import anbrain.qa.rococo.service.grpc.ArtistGrpcClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistGrpcClientTests {

    @Mock
    private ArtistServiceGrpc.ArtistServiceBlockingStub artistStub;

    @InjectMocks
    private ArtistGrpcClient artistGrpcClient;

    @Captor
    private ArgumentCaptor<ArtistRequest> artistRequestCaptor;
    @Captor
    private ArgumentCaptor<AllArtistsRequest> allArtistsRequestCaptor;
    @Captor
    private ArgumentCaptor<SearchArtistsRequest> searchArtistsRequestCaptor;
    @Captor
    private ArgumentCaptor<CreateArtistRequest> createArtistRequestCaptor;
    @Captor
    private ArgumentCaptor<UpdateArtistRequest> updateArtistRequestCaptor;

    private final UUID testId = UUID.randomUUID();
    private final String testName = "Test Artist";
    private final String testBiography = "Test Biography";
    private final String testPhoto = "photo1";

    @Test
    void getArtist_shouldReturnArtistWhenFound() {
        ArtistResponse response = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        when(artistStub.getArtist(any(ArtistRequest.class))).thenReturn(response);

        ArtistJson result = artistGrpcClient.getArtist(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testName, result.name());
        assertEquals(testBiography, result.biography());
        assertEquals(testPhoto, result.photo());

        verify(artistStub).getArtist(artistRequestCaptor.capture());
        assertEquals(testId.toString(), artistRequestCaptor.getValue().getId());
    }

    @Test
    void getArtist_shouldThrowExceptionWhenNotFound() {
        when(artistStub.getArtist(any(ArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        RococoNotFoundException ex = assertThrows(RococoNotFoundException.class, () -> artistGrpcClient.getArtist(testId));
        assertEquals("Художник с ID "+testId+" не найден",ex.getMessage());
    }

    @Test
    void getAllArtists_shouldReturnPageOfArtists() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        ArtistResponse artistResponse = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        AllArtistsResponse response = AllArtistsResponse.newBuilder()
                .addArtists(artistResponse)
                .setTotalCount(1)
                .build();

        when(artistStub.getAllArtists(any(AllArtistsRequest.class))).thenReturn(response);

        Page<ArtistJson> result = artistGrpcClient.getAllArtists(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        ArtistJson artistJson = result.getContent().getFirst();
        assertEquals(testId, artistJson.id());
        assertEquals(testName, artistJson.name());
        assertEquals(testBiography, artistJson.biography());
        assertEquals(testPhoto, artistJson.photo());

        verify(artistStub).getAllArtists(allArtistsRequestCaptor.capture());
        assertEquals(page, allArtistsRequestCaptor.getValue().getPage());
        assertEquals(size, allArtistsRequestCaptor.getValue().getSize());
    }

    @Test
    void searchArtistsByName_shouldReturnFilteredArtists() {
        String searchName = "search";
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        ArtistResponse artistResponse = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        AllArtistsResponse response = AllArtistsResponse.newBuilder()
                .addArtists(artistResponse)
                .setTotalCount(1)
                .build();

        when(artistStub.searchArtistsByName(any(SearchArtistsRequest.class))).thenReturn(response);

        Page<ArtistJson> result = artistGrpcClient.searchArtistsByName(searchName, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(artistStub).searchArtistsByName(searchArtistsRequestCaptor.capture());
        assertEquals(searchName, searchArtistsRequestCaptor.getValue().getName());
        assertEquals(page, searchArtistsRequestCaptor.getValue().getPage());
        assertEquals(size, searchArtistsRequestCaptor.getValue().getSize());
    }

    @Test
    void createArtist_shouldReturnCreatedArtist() {
        ArtistJson newArtist = new ArtistJson(null, testName, testBiography, testPhoto);

        ArtistResponse response = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        when(artistStub.createArtist(any(CreateArtistRequest.class))).thenReturn(response);

        ArtistJson result = artistGrpcClient.createArtist(newArtist);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testName, result.name());
        assertEquals(testBiography, result.biography());
        assertEquals(testPhoto, result.photo());

        verify(artistStub).createArtist(createArtistRequestCaptor.capture());
        assertEquals(testName, createArtistRequestCaptor.getValue().getName());
        assertEquals(testBiography, createArtistRequestCaptor.getValue().getBiography());
        assertEquals(testPhoto, createArtistRequestCaptor.getValue().getPhoto());
    }

    @Test
    void updateArtist_shouldReturnUpdatedArtist() {
        ArtistJson updatedArtist = new ArtistJson(testId, testName, testBiography, testPhoto);

        ArtistResponse response = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        when(artistStub.updateArtist(any(UpdateArtistRequest.class))).thenReturn(response);

        ArtistJson result = artistGrpcClient.updateArtist(updatedArtist);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testName, result.name());
        assertEquals(testBiography, result.biography());
        assertEquals(testPhoto, result.photo());

        verify(artistStub).updateArtist(updateArtistRequestCaptor.capture());
        assertEquals(testId.toString(), updateArtistRequestCaptor.getValue().getId());
        assertEquals(testName, updateArtistRequestCaptor.getValue().getName());
        assertEquals(testBiography, updateArtistRequestCaptor.getValue().getBiography());
        assertEquals(testPhoto, updateArtistRequestCaptor.getValue().getPhoto());
    }

    @Test
    void toArtistJson_shouldConvertResponseToJson() {
        ArtistResponse response = ArtistResponse.newBuilder()
                .setId(testId.toString())
                .setName(testName)
                .setBiography(testBiography)
                .setPhoto(testPhoto)
                .build();

        ArtistJson result = artistGrpcClient.toArtistJson(response);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testName, result.name());
        assertEquals(testBiography, result.biography());
        assertEquals(testPhoto, result.photo());
    }

    @Test
    void getAllArtists_shouldThrowServiceUnavailableExceptionWhenServiceUnavailable() {
        Pageable pageable = PageRequest.of(0, 10);

        when(artistStub.getAllArtists(any(AllArtistsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> artistGrpcClient.getAllArtists(pageable));
        assertEquals("Сервис временно недоступен", ex.getMessage());
    }

    @Test
    void getAllArtists_shouldThrowTimeoutExceptionWhenDeadlineExceeded() {
        Pageable pageable = PageRequest.of(0, 10);

        when(artistStub.getAllArtists(any(AllArtistsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> artistGrpcClient.getAllArtists(pageable));
        assertEquals("Превышено время ожидания ответа от сервиса", ex.getMessage());
    }

    @Test
    void getAllArtists_shouldThrowValidationExceptionWhenInvalidArgument() {
        Pageable pageable = PageRequest.of(0, 10);

        when(artistStub.getAllArtists(any(AllArtistsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        RococoValidationException ex = assertThrows(RococoValidationException.class,
                () -> artistGrpcClient.getAllArtists(pageable));
        assertEquals("Ошибка валидации данных: Список художников - страница 0", ex.getMessage());
    }

    @Test
    void getAllArtists_shouldThrowAccessDeniedExceptionWhenPermissionDenied() {
        Pageable pageable = PageRequest.of(0, 10);

        when(artistStub.getAllArtists(any(AllArtistsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

        RococoAccessDeniedException ex = assertThrows(RococoAccessDeniedException.class,
                () -> artistGrpcClient.getAllArtists(pageable));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void searchArtistsByName_shouldThrowNotFoundExceptionWhenArtistNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        String searchName = "unknown";

        when(artistStub.searchArtistsByName(any(SearchArtistsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        RococoNotFoundException ex = assertThrows(RococoNotFoundException.class,
                () -> artistGrpcClient.searchArtistsByName(searchName, pageable));
        assertEquals("Поиск художников с ID unknown не найден", ex.getMessage());
    }

    @Test
    void createArtist_shouldThrowConflictExceptionWhenArtistAlreadyExists() {
        ArtistJson newArtist = new ArtistJson(null, testName, testBiography, testPhoto);

        when(artistStub.createArtist(any(CreateArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.ALREADY_EXISTS));

        RococoConflictException ex = assertThrows(RococoConflictException.class,
                () -> artistGrpcClient.createArtist(newArtist));
        assertEquals("Создание художника с такими параметрами уже существует: Test Artist", ex.getMessage());
    }

    @Test
    void updateArtist_shouldThrowRuntimeExceptionWhenUnknownErrorOccurs() {
        ArtistJson updatedArtist = new ArtistJson(testId, testName, testBiography, testPhoto);

        when(artistStub.updateArtist(any(UpdateArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNKNOWN));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> artistGrpcClient.updateArtist(updatedArtist));
        assertEquals("Ошибка при обработке Обновление художника: " + testId + " - UNKNOWN", ex.getMessage());
    }
}