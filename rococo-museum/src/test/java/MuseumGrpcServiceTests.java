import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.MuseumEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.service.MuseumDatabaseService;
import anbrain.qa.rococo.service.MuseumGrpcService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MuseumGrpcServiceTests {

    private UUID museumId;
    private UUID museumId2;
    CountryEntity countryEntity;
    MuseumEntity museumEntity;
    MuseumEntity museumEntity2;
    MuseumEntity updatedMuseum;

    @BeforeEach
    void setUp() {
        museumId = UUID.randomUUID();
        museumId2 = UUID.randomUUID();
        UUID countryId = UUID.randomUUID();

        countryEntity = new CountryEntity();
        countryEntity.setId(countryId);
        countryEntity.setName("Country");

        museumEntity = new MuseumEntity();
        museumEntity.setId(museumId);
        museumEntity.setTitle("Test Museum");
        museumEntity.setDescription("Test Description");
        museumEntity.setPhoto("photo".getBytes(StandardCharsets.UTF_8));
        museumEntity.setCity("City");
        museumEntity.setCountry(countryEntity);

        museumEntity2 = new MuseumEntity();
        museumEntity2.setId(museumId2);
        museumEntity2.setTitle("Test Museum");
        museumEntity2.setDescription("Test Description");
        museumEntity2.setPhoto("photo".getBytes(StandardCharsets.UTF_8));
        museumEntity2.setCity("City");
        museumEntity2.setCountry(countryEntity);

        updatedMuseum = new MuseumEntity();
        updatedMuseum.setId(museumId);
        updatedMuseum.setTitle("Updated Museum");
        updatedMuseum.setDescription("Updated Description");
        updatedMuseum.setPhoto("new photo".getBytes(StandardCharsets.UTF_8));
        updatedMuseum.setCity("Updated City");
        updatedMuseum.setCountry(countryEntity);
    }

    @Mock
    private MuseumDatabaseService museumDatabaseService;

    @Mock
    private StreamObserver<MuseumResponse> museumResponseObserver;

    @Mock
    private StreamObserver<AllMuseumsResponse> allMuseumsResponseObserver;

    @Mock
    private StreamObserver<SearchMuseumsResponse> searchMuseumsResponseObserver;

    @InjectMocks
    private MuseumGrpcService museumGrpcService;

    @Test
    void getMuseum_shouldReturnMuseumWhenExists() {

        when(museumDatabaseService.findById(museumId)).thenReturn(museumEntity);

        MuseumRequest request = MuseumRequest.newBuilder()
                .setId(museumId.toString())
                .build();

        museumGrpcService.getMuseum(request, museumResponseObserver);

        ArgumentCaptor<MuseumResponse> captor = ArgumentCaptor.forClass(MuseumResponse.class);
        verify(museumResponseObserver).onNext(captor.capture());
        verify(museumResponseObserver).onCompleted();

        MuseumResponse response = captor.getValue();
        assertEquals(museumId.toString(), response.getId());
        assertEquals("Test Museum", response.getTitle());
    }

//    @Test
//    void getMuseum_shouldHandleEntityNotFoundException() {
//        when(museumDatabaseService.findById(museumId))
//                .thenThrow(new EntityNotFoundException("Музей не найден с id: " + museumId));
//
//        MuseumRequest request = MuseumRequest.newBuilder()
//                .setId(museumId.toString())
//                .build();
//
//        museumGrpcService.getMuseum(request, museumResponseObserver);
//
//        ArgumentCaptor<Throwable> captor = ArgumentCaptor.forClass(Throwable.class);
//        verify(museumResponseObserver).onError(captor.capture());
//
//        Throwable error = captor.getValue();
//        assertInstanceOf(StatusRuntimeException.class, error);
//        assertTrue(error.getMessage().contains("Музей с ID"));
//    }

    @Test
    void getAllMuseums_shouldReturnPageOfMuseums() {
        PageRequest pageable = PageRequest.of(0, 10);
        List<MuseumEntity> museums = List.of(
                museumEntity,
                museumEntity2
        );
        Page<MuseumEntity> page = new PageImpl<>(museums);

        when(museumDatabaseService.getAll(pageable)).thenReturn(page);

        AllMuseumsRequest request = AllMuseumsRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        museumGrpcService.getAllMuseums(request, allMuseumsResponseObserver);

        ArgumentCaptor<AllMuseumsResponse> captor = ArgumentCaptor.forClass(AllMuseumsResponse.class);
        verify(allMuseumsResponseObserver).onNext(captor.capture());
        verify(allMuseumsResponseObserver).onCompleted();

        AllMuseumsResponse response = captor.getValue();
        assertEquals(2, response.getMuseumsCount());
        assertEquals(2, response.getTotalCount());
    }

    @Test
    void searchMuseumsByTitle_shouldReturnMatchingMuseums() {
        String searchTitle = "test";
        List<MuseumEntity> museums = List.of(
                museumEntity,
                museumEntity2
        );

        when(museumDatabaseService.searchByTitle(searchTitle)).thenReturn(museums);

        SearchMuseumsRequest request = SearchMuseumsRequest.newBuilder()
                .setTitle(searchTitle)
                .build();

        museumGrpcService.searchMuseumsByTitle(request, searchMuseumsResponseObserver);

        ArgumentCaptor<SearchMuseumsResponse> captor = ArgumentCaptor.forClass(SearchMuseumsResponse.class);
        verify(searchMuseumsResponseObserver).onNext(captor.capture());
        verify(searchMuseumsResponseObserver).onCompleted();

        SearchMuseumsResponse response = captor.getValue();
        assertEquals(2, response.getMuseumsCount());
    }

    @Test
    void searchMuseumsByTitle_shouldThrowExceptionWhenTitleIsBlank() {
        SearchMuseumsRequest request = SearchMuseumsRequest.newBuilder()
                .setTitle("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> museumGrpcService.searchMuseumsByTitle(request, searchMuseumsResponseObserver));

        assertEquals("Поисковый запрос не может быть пустым", exception.getMessage());
        verify(searchMuseumsResponseObserver, never()).onNext(any());
        verify(searchMuseumsResponseObserver, never()).onCompleted();
    }

    @Test
    void createMuseum_shouldSaveNewMuseum() {
        when(museumDatabaseService.create(any())).thenReturn(museumEntity);

        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle(museumEntity.getTitle())
                .setDescription(museumEntity.getDescription())
                .setPhoto(Arrays.toString(museumEntity.getPhoto()))
                .setGeo(Geo.newBuilder()
                        .setCity(museumEntity.getCity())
                        .setCountry(Country.newBuilder()
                                .setName(countryEntity.getName())
                                .setId(String.valueOf(countryEntity.getId()))
                                .build())
                        .build())
                .build();

        museumGrpcService.createMuseum(request, museumResponseObserver);

        ArgumentCaptor<MuseumResponse> captor = ArgumentCaptor.forClass(MuseumResponse.class);
        verify(museumResponseObserver).onNext(captor.capture());
        verify(museumResponseObserver).onCompleted();

        MuseumResponse response = captor.getValue();
        assertEquals(museumEntity.getTitle(), response.getTitle());
    }

    @Test
    void createMuseum_shouldThrowExceptionWhenTitleIsBlank() {
        CreateMuseumRequest request = CreateMuseumRequest.newBuilder()
                .setTitle("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> museumGrpcService.createMuseum(request, museumResponseObserver));

        assertEquals("Название музея обязательно для заполнения", exception.getMessage());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
    }

    @Test
    void updateMuseum_shouldUpdateExistingMuseum() {
        when(museumDatabaseService.update(any())).thenReturn(updatedMuseum);

        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(museumId.toString())
                .setTitle("Updated Museum")
                .setDescription("Updated Description")
                .setPhoto("new photo")
                .setGeo(Geo.newBuilder()
                        .setCity("Updated City")
                        .setCountry(Country.newBuilder()
                                .setId(String.valueOf(countryEntity.getId()))
                                .setName(countryEntity.getName())
                                .build())
                        .build())
                .build();

        museumGrpcService.updateMuseum(request, museumResponseObserver);

        ArgumentCaptor<MuseumResponse> captor = ArgumentCaptor.forClass(MuseumResponse.class);
        verify(museumResponseObserver).onNext(captor.capture());
        verify(museumResponseObserver).onCompleted();

        MuseumResponse response = captor.getValue();
        assertEquals(museumId.toString(), response.getId());
        assertEquals("Updated Museum", response.getTitle());
    }

    @Test
    void updateMuseum_shouldThrowExceptionWhenTitleIsBlank() {
        UpdateMuseumRequest request = UpdateMuseumRequest.newBuilder()
                .setId(String.valueOf(museumId2))
                .setTitle("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> museumGrpcService.updateMuseum(request, museumResponseObserver));

        assertEquals("Название музея обязательно для заполнения", exception.getMessage());
        verify(museumResponseObserver, never()).onNext(any());
        verify(museumResponseObserver, never()).onCompleted();
    }

}