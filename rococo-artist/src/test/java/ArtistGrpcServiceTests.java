import anbrain.qa.rococo.data.ArtistEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.service.ArtistDatabaseService;
import anbrain.qa.rococo.service.ArtistGrpcService;
import anbrain.qa.rococo.utils.GrpcArtistConverter;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistGrpcServiceTests {

    @Mock
    private ArtistDatabaseService artistDatabaseService;

    @Mock
    private GrpcArtistConverter grpcArtistConverter;

    @Mock
    private StreamObserver<ArtistResponse> artistResponseObserver;

    @Mock
    private StreamObserver<AllArtistsResponse> allArtistsResponseObserver;

    @InjectMocks
    private ArtistGrpcService artistGrpcService;

    private UUID existingArtistId;
    private ArtistEntity testArtist;
    private ArtistResponse testArtistResponse;
    private AllArtistsResponse testAllArtistsResponse;

    @BeforeEach
    void setUp() {
        existingArtistId = UUID.randomUUID();

        testArtist = new ArtistEntity();
        testArtist.setId(existingArtistId);
        testArtist.setName("Test Artist");
        testArtist.setBiography("Test Biography");
        testArtist.setPhoto("photo".getBytes());

        testArtistResponse = ArtistResponse.newBuilder()
                .setId(existingArtistId.toString())
                .setName(testArtist.getName())
                .setBiography(testArtist.getBiography())
                .setPhoto(new String(testArtist.getPhoto()))
                .build();

        testAllArtistsResponse = AllArtistsResponse.newBuilder()
                .addAllArtists(List.of(testArtistResponse))
                .setTotalCount(1)
                .build();
    }

    @Test
    void getArtist_shouldReturnArtistResponseWhenExists() {
        ArtistRequest request = ArtistRequest.newBuilder()
                .setId(existingArtistId.toString())
                .build();

        when(artistDatabaseService.getArtist(existingArtistId)).thenReturn(testArtist);
        when(grpcArtistConverter.entityToGrpcResponse(testArtist)).thenReturn(testArtistResponse);

        artistGrpcService.getArtist(request, artistResponseObserver);

        verify(artistDatabaseService, times(1)).getArtist(existingArtistId);
        verify(grpcArtistConverter, times(1)).entityToGrpcResponse(testArtist);
        verify(artistResponseObserver, times(1)).onNext(testArtistResponse);
        verify(artistResponseObserver, times(1)).onCompleted();
    }

    @Test
    void getAllArtists_shouldReturnPageOfArtists() {
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArtistEntity> artistPage = new PageImpl<>(List.of(testArtist));

        AllArtistsRequest request = AllArtistsRequest.newBuilder()
                .setPage(page)
                .setSize(size)
                .build();

        when(artistDatabaseService.getAllArtists(pageRequest)).thenReturn(artistPage);
        when(grpcArtistConverter.pageToGrpcResponse(artistPage)).thenReturn(testAllArtistsResponse);

        artistGrpcService.getAllArtists(request, allArtistsResponseObserver);

        verify(artistDatabaseService, times(1)).getAllArtists(pageRequest);
        verify(grpcArtistConverter, times(1)).pageToGrpcResponse(artistPage);
        verify(allArtistsResponseObserver, times(1)).onNext(testAllArtistsResponse);
        verify(allArtistsResponseObserver, times(1)).onCompleted();
    }

    @Test
    void searchArtistsByName_shouldReturnFilteredArtists() {
        String searchName = "test";
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ArtistEntity> artistPage = new PageImpl<>(List.of(testArtist));

        SearchArtistsRequest request = SearchArtistsRequest.newBuilder()
                .setName(searchName)
                .setPage(page)
                .setSize(size)
                .build();

        when(artistDatabaseService.searchArtistsByName(searchName, pageRequest)).thenReturn(artistPage);
        when(grpcArtistConverter.pageToGrpcResponse(artistPage)).thenReturn(testAllArtistsResponse);

        artistGrpcService.searchArtistsByName(request, allArtistsResponseObserver);

        verify(artistDatabaseService, times(1)).searchArtistsByName(searchName, pageRequest);
        verify(grpcArtistConverter, times(1)).pageToGrpcResponse(artistPage);
        verify(allArtistsResponseObserver, times(1)).onNext(testAllArtistsResponse);
        verify(allArtistsResponseObserver, times(1)).onCompleted();
    }

    @Test
    void searchArtistsByName_shouldThrowExceptionWhenNameIsBlank() {
        SearchArtistsRequest request = SearchArtistsRequest.newBuilder()
                .setName("")
                .setPage(0)
                .setSize(10)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.searchArtistsByName(request, allArtistsResponseObserver)
        );
        assertEquals("Поисковый запрос не может быть пустым", exception.getMessage());

        verify(artistDatabaseService, never()).searchArtistsByName(any(), any());
        verify(allArtistsResponseObserver, never()).onNext(any());
        verify(allArtistsResponseObserver, never()).onCompleted();
    }

    @Test
    void createArtist_shouldSaveNewArtistAndReturnResponse() {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName(testArtist.getName())
                .setBiography(testArtist.getBiography())
                .setPhoto(new String(testArtist.getPhoto()))
                .build();

        when(artistDatabaseService.createArtist(
                testArtist.getName(),
                testArtist.getBiography(),
                testArtist.getPhoto()
        )).thenReturn(testArtist);
        when(grpcArtistConverter.entityToGrpcResponse(testArtist)).thenReturn(testArtistResponse);

        artistGrpcService.createArtist(request, artistResponseObserver);

        verify(artistDatabaseService, times(1)).createArtist(
                testArtist.getName(),
                testArtist.getBiography(),
                testArtist.getPhoto()
        );
        verify(grpcArtistConverter, times(1)).entityToGrpcResponse(testArtist);
        verify(artistResponseObserver, times(1)).onNext(testArtistResponse);
        verify(artistResponseObserver, times(1)).onCompleted();
    }

    @Test
    void createArtist_shouldThrowExceptionWhenNameIsBlank() {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("")
                .setBiography("Bio")
                .setPhoto("photo")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.createArtist(request, artistResponseObserver)
        );
        assertEquals("Имя художника обязательно для заполнения", exception.getMessage());

        verify(artistDatabaseService, never()).createArtist(any(), any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }

    @Test
    void createArtist_shouldThrowExceptionWhenBiographyIsBlank() {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Name")
                .setBiography("")
                .setPhoto("photo")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.createArtist(request, artistResponseObserver)
        );
        assertEquals("Биография художника обязательна для заполнения", exception.getMessage());

        verify(artistDatabaseService, never()).createArtist(any(), any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }

    @Test
    void createArtist_shouldThrowExceptionWhenPhotoIsEmpty() {
        CreateArtistRequest request = CreateArtistRequest.newBuilder()
                .setName("Name")
                .setBiography("Bio")
                .setPhoto("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.createArtist(request, artistResponseObserver)
        );
        assertEquals("Фото художника обязательно", exception.getMessage());

        verify(artistDatabaseService, never()).createArtist(any(), any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }

    @Test
    void updateArtist_shouldUpdateExistingArtistAndReturnResponse() {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(existingArtistId.toString())
                .setName("Updated Name")
                .setBiography("Updated Bio")
                .setPhoto("new photo")
                .build();

        ArtistEntity updatedArtist = new ArtistEntity();
        updatedArtist.setId(existingArtistId);
        updatedArtist.setName(request.getName());
        updatedArtist.setBiography(request.getBiography());
        updatedArtist.setPhoto(request.getPhoto().getBytes(StandardCharsets.UTF_8));

        ArtistResponse updatedResponse = ArtistResponse.newBuilder()
                .setId(existingArtistId.toString())
                .setName(request.getName())
                .setBiography(request.getBiography())
                .setPhoto(request.getPhoto())
                .build();

        when(artistDatabaseService.updateArtist(
                existingArtistId,
                request.getName(),
                request.getBiography(),
                request.getPhoto().getBytes(StandardCharsets.UTF_8)
        )).thenReturn(updatedArtist);
        when(grpcArtistConverter.entityToGrpcResponse(updatedArtist)).thenReturn(updatedResponse);

        artistGrpcService.updateArtist(request, artistResponseObserver);

        verify(artistDatabaseService, times(1)).updateArtist(
                existingArtistId,
                request.getName(),
                request.getBiography(),
                request.getPhoto().getBytes(StandardCharsets.UTF_8)
        );
        verify(grpcArtistConverter, times(1)).entityToGrpcResponse(updatedArtist);
        verify(artistResponseObserver, times(1)).onNext(updatedResponse);
        verify(artistResponseObserver, times(1)).onCompleted();
    }

    @Test
    void updateArtist_shouldUpdateWithoutPhotoWhenPhotoIsEmpty() {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(existingArtistId.toString())
                .setName("Updated Name")
                .setBiography("Updated Bio")
                .setPhoto("")
                .build();

        ArtistEntity updatedArtist = new ArtistEntity();
        updatedArtist.setId(existingArtistId);
        updatedArtist.setName(request.getName());
        updatedArtist.setBiography(request.getBiography());
        updatedArtist.setPhoto(testArtist.getPhoto()); // original photo remains

        ArtistResponse updatedResponse = ArtistResponse.newBuilder()
                .setId(existingArtistId.toString())
                .setName(request.getName())
                .setBiography(request.getBiography())
                .setPhoto(new String(testArtist.getPhoto()))
                .build();

        when(artistDatabaseService.updateArtist(
                existingArtistId,
                request.getName(),
                request.getBiography(),
                null
        )).thenReturn(updatedArtist);
        when(grpcArtistConverter.entityToGrpcResponse(updatedArtist)).thenReturn(updatedResponse);

        artistGrpcService.updateArtist(request, artistResponseObserver);

        verify(artistDatabaseService, times(1)).updateArtist(
                existingArtistId,
                request.getName(),
                request.getBiography(),
                null
        );
        verify(grpcArtistConverter, times(1)).entityToGrpcResponse(updatedArtist);
        verify(artistResponseObserver, times(1)).onNext(updatedResponse);
        verify(artistResponseObserver, times(1)).onCompleted();
    }

    @Test
    void updateArtist_shouldThrowExceptionWhenNameIsBlank() {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(existingArtistId.toString())
                .setName("")
                .setBiography("Bio")
                .setPhoto("photo")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.updateArtist(request, artistResponseObserver)
        );
        assertEquals("Имя художника обязательно для заполнения", exception.getMessage());

        verify(artistDatabaseService, never()).updateArtist(any(), any(), any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }

    @Test
    void updateArtist_shouldThrowExceptionWhenBiographyIsBlank() {
        UpdateArtistRequest request = UpdateArtistRequest.newBuilder()
                .setId(existingArtistId.toString())
                .setName("Name")
                .setBiography("")
                .setPhoto("photo")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                artistGrpcService.updateArtist(request, artistResponseObserver)
        );
        assertEquals("Биография художника обязательна для заполнения", exception.getMessage());

        verify(artistDatabaseService, never()).updateArtist(any(), any(), any(), any());
        verify(artistResponseObserver, never()).onNext(any());
        verify(artistResponseObserver, never()).onCompleted();
    }

}