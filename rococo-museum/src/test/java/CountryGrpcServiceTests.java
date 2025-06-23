import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.service.CountryDatabaseService;
import anbrain.qa.rococo.service.CountryGrpcService;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryGrpcServiceTests {

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

    @Mock
    private CountryDatabaseService countryDatabaseService;

    @Mock
    private StreamObserver<AllCountriesResponse> responseObserver;

    @InjectMocks
    private CountryGrpcService countryGrpcService;

    @Test
    void getAllCountries_shouldReturnPageOfCountries() {
        AllCountriesRequest request = AllCountriesRequest.newBuilder()
                .setPage(0)
                .setSize(10)
                .build();

        List<CountryEntity> countries = List.of(
                entity1,
                entity2
        );
        Page<CountryEntity> page = new PageImpl<>(countries);

        when(countryDatabaseService.getAllCountries(any())).thenReturn(page);

        countryGrpcService.getAllCountries(request, responseObserver);

        ArgumentCaptor<AllCountriesResponse> captor = ArgumentCaptor.forClass(AllCountriesResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        AllCountriesResponse response = captor.getValue();
        assertEquals(2, response.getCountriesCount());
        assertEquals(2, response.getTotalCount());
    }
}