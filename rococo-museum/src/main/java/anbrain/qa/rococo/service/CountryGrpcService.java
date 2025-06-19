package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@GrpcService
@RequiredArgsConstructor
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryDatabaseService countryDatabaseService;

    @Override
    public void getAllCountries(@Nonnull AllCountriesRequest request, @Nonnull StreamObserver<AllCountriesResponse> responseObserver) {
        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CountryEntity> page = countryDatabaseService.getAllCountries(pageable);

        responseObserver.onNext(AllCountriesResponse.newBuilder()
                .addAllCountries(page.getContent().stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .setTotalCount((int) page.getTotalElements())
                .build());
        responseObserver.onCompleted();
    }

    @Nonnull
    private Country toGrpcResponse(@Nonnull CountryEntity entity) {
        return Country.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .build();
    }
}