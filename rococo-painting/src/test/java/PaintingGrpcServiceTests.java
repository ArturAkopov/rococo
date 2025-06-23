import anbrain.qa.rococo.data.PaintingEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.service.PaintingDatabaseService;
import anbrain.qa.rococo.service.PaintingGrpcService;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaintingGrpcServiceTests {

    @Mock
    private PaintingDatabaseService paintingDatabaseService;

    @Mock
    private StreamObserver<PaintingResponse> paintingResponseObserver;

    @Mock
    private StreamObserver<AllPaintingsResponse> allPaintingsResponseObserver;

    @InjectMocks
    private PaintingGrpcService paintingGrpcService;

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
    void shouldSuccessfullyGetPainting() {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(testId.toString())
                .build();

        when(paintingDatabaseService.getPainting(testId)).thenReturn(testPainting);

        paintingGrpcService.getPainting(request, paintingResponseObserver);

        ArgumentCaptor<PaintingResponse> captor = ArgumentCaptor.forClass(PaintingResponse.class);
        verify(paintingResponseObserver).onNext(captor.capture());
        verify(paintingResponseObserver).onCompleted();

        PaintingResponse response = captor.getValue();
        assertEquals(testId.toString(), response.getId());
        assertEquals("Test Painting", response.getTitle());
    }

    @Test
    void shouldHandleErrorWhenPaintingNotFound() {
        PaintingRequest request = PaintingRequest.newBuilder()
                .setId(testId.toString())
                .build();

        when(paintingDatabaseService.getPainting(testId))
                .thenThrow(new EntityNotFoundException("Картина не найдена с id: " + testId));

        assertThrows(EntityNotFoundException.class,
                () -> paintingGrpcService.getPainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

    @Test
    void shouldSuccessfullyGetAllPaintings() {
        AllPaintingsRequest request = AllPaintingsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        Page<PaintingEntity> page = new PageImpl<>(Collections.singletonList(testPainting));
        when(paintingDatabaseService.getAllPaintings(any())).thenReturn(page);

        paintingGrpcService.getAllPaintings(request, allPaintingsResponseObserver);

        ArgumentCaptor<AllPaintingsResponse> captor = ArgumentCaptor.forClass(AllPaintingsResponse.class);
        verify(allPaintingsResponseObserver).onNext(captor.capture());
        verify(allPaintingsResponseObserver).onCompleted();

        AllPaintingsResponse response = captor.getValue();
        assertEquals(1, response.getTotalCount());
        assertEquals(1, response.getPaintingsCount());
    }

    @Test
    void shouldSuccessfullyGetPaintingsByArtist() {
        PaintingsByArtistRequest request = PaintingsByArtistRequest.newBuilder()
                .setArtistId(artistId.toString())
                .setPage(0)
                .setSize(10)
                .build();

        Page<PaintingEntity> page = new PageImpl<>(Collections.singletonList(testPainting));
        when(paintingDatabaseService.getPaintingsByArtist(eq(artistId), any())).thenReturn(page);

        paintingGrpcService.getPaintingsByArtist(request, allPaintingsResponseObserver);

        ArgumentCaptor<AllPaintingsResponse> captor = ArgumentCaptor.forClass(AllPaintingsResponse.class);
        verify(allPaintingsResponseObserver).onNext(captor.capture());
        verify(allPaintingsResponseObserver).onCompleted();

        AllPaintingsResponse response = captor.getValue();
        assertEquals(1, response.getTotalCount());
        assertEquals(1, response.getPaintingsCount());
    }

    @Test
    void shouldSuccessfullyCreatePainting() {
        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle("New Painting")
                .setDescription("New Description")
                .setContent("new content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        when(paintingDatabaseService.createPainting(any())).thenReturn(testPainting);

        paintingGrpcService.createPainting(request, paintingResponseObserver);

        ArgumentCaptor<PaintingResponse> captor = ArgumentCaptor.forClass(PaintingResponse.class);
        verify(paintingResponseObserver).onNext(captor.capture());
        verify(paintingResponseObserver).onCompleted();

        PaintingResponse response = captor.getValue();
        assertEquals(testId.toString(), response.getId());
        assertEquals("Test Painting", response.getTitle());
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaintingWithBlankTitle() {
        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle(" ")
                .setDescription("New Description")
                .setContent("new content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> paintingGrpcService.createPainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

    @Test
    void shouldSuccessfullyUpdatePainting() {
        UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setId(testId.toString())
                .setTitle("Updated Painting")
                .setDescription("Updated Description")
                .setContent("updated content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        when(paintingDatabaseService.getPainting(testId)).thenReturn(testPainting);
        when(paintingDatabaseService.updatePainting(any())).thenAnswer(i ->
                i.<PaintingEntity>getArgument(0)
        );

        paintingGrpcService.updatePainting(request, paintingResponseObserver);

        ArgumentCaptor<PaintingResponse> captor = ArgumentCaptor.forClass(PaintingResponse.class);
        verify(paintingResponseObserver).onNext(captor.capture());
        verify(paintingResponseObserver).onCompleted();

        PaintingResponse response = captor.getValue();
        assertEquals(testId.toString(), response.getId());
        assertEquals("Updated Painting", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPaintingWithBlankTitle() {
        UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setId(testId.toString())
                .setTitle(" ")
                .setDescription("Updated Description")
                .setContent("updated content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> paintingGrpcService.updatePainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaintingWithEmptyTitle() {
        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle("") // пустая строка
                .setDescription("New Description")
                .setContent("new content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> paintingGrpcService.createPainting(request, paintingResponseObserver));

        assertEquals("Название картины обязательно для заполнения", exception.getMessage());
        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
        verify(paintingDatabaseService, never()).createPainting(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentPainting() {
        UpdatePaintingRequest request = UpdatePaintingRequest.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setTitle("Updated Title")
                .setDescription("Updated Description")
                .setContent("updated content")
                .setArtistId(artistId.toString())
                .setMuseumId(museumId.toString())
                .build();

        when(paintingDatabaseService.getPainting(any(UUID.class)))
                .thenThrow(new EntityNotFoundException("Картина не найдена"));

        assertThrows(EntityNotFoundException.class,
                () -> paintingGrpcService.updatePainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaintingWithInvalidArtistId() {
        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle("Title")
                .setDescription("Description")
                .setContent("content")
                .setArtistId("invalid-uuid")
                .setMuseumId(museumId.toString())
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> paintingGrpcService.createPainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

    @Test
    void shouldThrowExceptionWhenCreatingPaintingWithInvalidMuseumId() {
        CreatePaintingRequest request = CreatePaintingRequest.newBuilder()
                .setTitle("Title")
                .setDescription("Description")
                .setContent("content")
                .setArtistId(artistId.toString())
                .setMuseumId("invalid-uuid")
                .build();

        assertThrows(IllegalArgumentException.class,
                () -> paintingGrpcService.createPainting(request, paintingResponseObserver));

        verify(paintingResponseObserver, never()).onNext(any());
        verify(paintingResponseObserver, never()).onCompleted();
    }

}