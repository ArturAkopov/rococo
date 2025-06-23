package service;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.exception.*;
import anbrain.qa.rococo.model.*;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.ArtistGrpcClient;
import anbrain.qa.rococo.service.grpc.MuseumGrpcClient;
import anbrain.qa.rococo.service.grpc.PaintingGrpcClient;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaintingGrpcClientTests {

    @Mock
    private PaintingServiceGrpc.PaintingServiceBlockingStub paintingStub;

    @Mock
    private ArtistGrpcClient artistGrpcClient;

    @Mock
    private MuseumGrpcClient museumGrpcClient;

    @InjectMocks
    private PaintingGrpcClient paintingGrpcClient;

    @Captor
    private ArgumentCaptor<PaintingRequest> paintingRequestCaptor;
    @Captor
    private ArgumentCaptor<AllPaintingsRequest> allPaintingsRequestCaptor;
    @Captor
    private ArgumentCaptor<PaintingsByArtistRequest> paintingsByArtistRequestCaptor;
    @Captor
    private ArgumentCaptor<CreatePaintingRequest> createPaintingRequestCaptor;
    @Captor
    private ArgumentCaptor<UpdatePaintingRequest> updatePaintingRequestCaptor;

    private final UUID testId = UUID.randomUUID();
    private final String testTitle = "Test Painting";
    private final String testDescription = "Test Description";
    private final String testContent = "content1";
    private final UUID testArtistId = UUID.randomUUID();
    private final UUID testMuseumId = UUID.randomUUID();
    private ArtistJson testArtist;
    private MuseumJson testMuseum;

    @BeforeEach
    void setUp() {
        testArtist = new ArtistJson(testArtistId, "Test Artist", "Artist Bio", "artist_photo");
        testMuseum = new MuseumJson(
                testMuseumId,
                "Test Museum",
                "Museum Description",
                "museum_photo",
                new GeoJson("Test City", new CountryJson(UUID.randomUUID(), "Test Country"))
        );
    }

    @Test
    void getPainting_shouldReturnPaintingWhenFound() {
        PaintingResponse response = createTestPaintingResponse();

        when(paintingStub.getPainting(any(PaintingRequest.class))).thenReturn(response);
        when(artistGrpcClient.getArtist(testArtistId)).thenReturn(testArtist);
        when(museumGrpcClient.getMuseum(testMuseumId)).thenReturn(testMuseum);

        PaintingJson result = paintingGrpcClient.getPainting(testId);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());
        assertEquals(testDescription, result.description());
        assertEquals(testContent, result.content());
        assertEquals(testArtistId, result.artist().id());
        assertEquals(testMuseumId, result.museum().id());

        verify(paintingStub).getPainting(paintingRequestCaptor.capture());
        assertEquals(testId.toString(), paintingRequestCaptor.getValue().getId());
    }

    @Test
    void getAllPaintings_shouldReturnPageOfPaintings() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        AllPaintingsResponse response = AllPaintingsResponse.newBuilder()
                .addPaintings(createTestPaintingResponse())
                .setTotalCount(1)
                .build();

        when(paintingStub.getAllPaintings(any(AllPaintingsRequest.class))).thenReturn(response);
        when(artistGrpcClient.getArtist(testArtistId)).thenReturn(testArtist);
        when(museumGrpcClient.getMuseum(testMuseumId)).thenReturn(testMuseum);

        RestPage<PaintingJson> result = paintingGrpcClient.getAllPaintings(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        PaintingJson paintingJson = result.getContent().getFirst();
        assertEquals(testId, paintingJson.id());
        assertEquals(testTitle, paintingJson.title());

        verify(paintingStub).getAllPaintings(allPaintingsRequestCaptor.capture());
        assertEquals(page, allPaintingsRequestCaptor.getValue().getPage());
        assertEquals(size, allPaintingsRequestCaptor.getValue().getSize());
    }

    @Test
    void getPaintingsByArtist_shouldReturnFilteredPaintings() {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        AllPaintingsResponse response = AllPaintingsResponse.newBuilder()
                .addPaintings(createTestPaintingResponse())
                .setTotalCount(1)
                .build();

        when(paintingStub.getPaintingsByArtist(any(PaintingsByArtistRequest.class))).thenReturn(response);
        when(artistGrpcClient.getArtist(testArtistId)).thenReturn(testArtist);
        when(museumGrpcClient.getMuseum(testMuseumId)).thenReturn(testMuseum);

        RestPage<PaintingJson> result = paintingGrpcClient.getPaintingsByArtist(testArtistId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        verify(paintingStub).getPaintingsByArtist(paintingsByArtistRequestCaptor.capture());
        assertEquals(testArtistId.toString(), paintingsByArtistRequestCaptor.getValue().getArtistId());
        assertEquals(page, paintingsByArtistRequestCaptor.getValue().getPage());
        assertEquals(size, paintingsByArtistRequestCaptor.getValue().getSize());
    }

    @Test
    void createPainting_shouldReturnCreatedPainting() {
        PaintingJson newPainting = new PaintingJson(
                null,
                testTitle,
                testDescription,
                testContent,
                testMuseum,
                testArtist
        );

        PaintingResponse response = createTestPaintingResponse();

        when(paintingStub.createPainting(any(CreatePaintingRequest.class))).thenReturn(response);
        when(artistGrpcClient.getArtist(testArtistId)).thenReturn(testArtist);
        when(museumGrpcClient.getMuseum(testMuseumId)).thenReturn(testMuseum);

        PaintingJson result = paintingGrpcClient.createPainting(newPainting);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());

        verify(paintingStub).createPainting(createPaintingRequestCaptor.capture());
        assertEquals(testTitle, createPaintingRequestCaptor.getValue().getTitle());
        assertEquals(testDescription, createPaintingRequestCaptor.getValue().getDescription());
        assertEquals(testContent, createPaintingRequestCaptor.getValue().getContent());
        assertEquals(testMuseumId.toString(), createPaintingRequestCaptor.getValue().getMuseumId());
        assertEquals(testArtistId.toString(), createPaintingRequestCaptor.getValue().getArtistId());
    }

    @Test
    void updatePainting_shouldReturnUpdatedPainting() {
        PaintingJson updatedPainting = new PaintingJson(
                testId,
                testTitle,
                testDescription,
                testContent,
                testMuseum,
                testArtist
        );

        PaintingResponse response = createTestPaintingResponse();

        when(paintingStub.updatePainting(any(UpdatePaintingRequest.class))).thenReturn(response);
        when(artistGrpcClient.getArtist(testArtistId)).thenReturn(testArtist);
        when(museumGrpcClient.getMuseum(testMuseumId)).thenReturn(testMuseum);

        PaintingJson result = paintingGrpcClient.updatePainting(updatedPainting);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());

        verify(paintingStub).updatePainting(updatePaintingRequestCaptor.capture());
        assertEquals(testId.toString(), updatePaintingRequestCaptor.getValue().getId());
        assertEquals(testTitle, updatePaintingRequestCaptor.getValue().getTitle());
        assertEquals(testDescription, updatePaintingRequestCaptor.getValue().getDescription());
        assertEquals(testContent, updatePaintingRequestCaptor.getValue().getContent());
        assertEquals(testMuseumId.toString(), updatePaintingRequestCaptor.getValue().getMuseumId());
        assertEquals(testArtistId.toString(), updatePaintingRequestCaptor.getValue().getArtistId());
    }

    @Test
    void getPainting_shouldThrowNotFoundExceptionWhenNotFound() {
        when(paintingStub.getPainting(any(PaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.NOT_FOUND));

        RococoNotFoundException ex = assertThrows(RococoNotFoundException.class,
                () -> paintingGrpcClient.getPainting(testId));
        assertEquals("Картина с ID " + testId + " не найден", ex.getMessage());
    }

    @Test
    void getPainting_shouldThrowServiceUnavailableExceptionWhenServiceUnavailable() {
        when(paintingStub.getPainting(any(PaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNAVAILABLE));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> paintingGrpcClient.getPainting(testId));
        assertEquals("Сервис временно недоступен", ex.getMessage());
    }

    @Test
    void getAllPaintings_shouldThrowValidationExceptionWhenInvalidArgument() {
        Pageable pageable = PageRequest.of(0, 10);

        when(paintingStub.getAllPaintings(any(AllPaintingsRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.INVALID_ARGUMENT));

        RococoValidationException ex = assertThrows(RococoValidationException.class,
                () -> paintingGrpcClient.getAllPaintings(pageable));
        assertEquals("Ошибка валидации данных: Картины - страница 0", ex.getMessage());
    }

    @Test
    void getPaintingsByArtist_shouldThrowAccessDeniedExceptionWhenPermissionDenied() {
        Pageable pageable = PageRequest.of(0, 10);

        when(paintingStub.getPaintingsByArtist(any(PaintingsByArtistRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.PERMISSION_DENIED));

        RococoAccessDeniedException ex = assertThrows(RococoAccessDeniedException.class,
                () -> paintingGrpcClient.getPaintingsByArtist(testArtistId, pageable));
        assertEquals("Доступ запрещен", ex.getMessage());
    }

    @Test
    void createPainting_shouldThrowConflictExceptionWhenAlreadyExists() {
        PaintingJson newPainting = new PaintingJson(
                null,
                testTitle,
                testDescription,
                testContent,
                testMuseum,
                testArtist
        );

        when(paintingStub.createPainting(any(CreatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.ALREADY_EXISTS));

        RococoConflictException ex = assertThrows(RococoConflictException.class,
                () -> paintingGrpcClient.createPainting(newPainting));
        assertEquals("Создание картины с такими параметрами уже существует: Test Painting", ex.getMessage());
    }

    @Test
    void updatePainting_shouldThrowTimeoutExceptionWhenDeadlineExceeded() {
        PaintingJson updatedPainting = new PaintingJson(
                testId,
                testTitle,
                testDescription,
                testContent,
                testMuseum,
                testArtist
        );

        when(paintingStub.updatePainting(any(UpdatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.DEADLINE_EXCEEDED));

        RococoServiceUnavailableException ex = assertThrows(RococoServiceUnavailableException.class,
                () -> paintingGrpcClient.updatePainting(updatedPainting));
        assertEquals("Превышено время ожидания ответа от сервиса", ex.getMessage());
    }

    @Test
    void updatePainting_shouldThrowRuntimeExceptionWhenUnknownError() {
        PaintingJson updatedPainting = new PaintingJson(
                testId,
                testTitle,
                testDescription,
                testContent,
                testMuseum,
                testArtist
        );

        when(paintingStub.updatePainting(any(UpdatePaintingRequest.class)))
                .thenThrow(new StatusRuntimeException(Status.UNKNOWN));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paintingGrpcClient.updatePainting(updatedPainting));
        assertTrue(ex.getMessage().contains("Ошибка при обработке Обновление картины: " + testId));
    }

    @Test
    void toPaintingJson_shouldConvertResponseToPaintingJson() {
        PaintingResponse response = createTestPaintingResponse();
        PaintingJson result = paintingGrpcClient.toPaintingJson(response, testMuseum, testArtist);

        assertNotNull(result);
        assertEquals(testId, result.id());
        assertEquals(testTitle, result.title());
        assertEquals(testDescription, result.description());
        assertEquals(testContent, result.content());
        assertEquals(testMuseumId, result.museum().id());
        assertEquals(testArtistId, result.artist().id());
    }

    @Nonnull
    private PaintingResponse createTestPaintingResponse() {
        return PaintingResponse.newBuilder()
                .setId(testId.toString())
                .setTitle(testTitle)
                .setDescription(testDescription)
                .setContent(testContent)
                .setArtistId(testArtistId.toString())
                .setMuseumId(testMuseumId.toString())
                .build();
    }
}