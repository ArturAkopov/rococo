package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.data.repository.CountryRepository;
import anbrain.qa.rococo.grpc.AllCountriesRequest;
import anbrain.qa.rococo.grpc.AllCountriesResponse;
import anbrain.qa.rococo.grpc.Country;
import anbrain.qa.rococo.grpc.CountryServiceGrpc;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryRepository countryRepository;

    @Override
    public void getAllCountries(@Nonnull AllCountriesRequest request, @Nonnull StreamObserver<AllCountriesResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CountryEntity> countryPage = countryRepository.findAll(pageable);

        AllCountriesResponse response = AllCountriesResponse.newBuilder()
                .addAllCountries(countryPage.getContent().stream()
                        .map(country -> Country.newBuilder()
                                .setId(country.getId().toString())
                                .setName(country.getName())
                                .build())
                        .toList())
                .setTotalCount((int) countryPage.getTotalElements())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
