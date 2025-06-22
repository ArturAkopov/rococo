package anbrain.qa.rococo.service;

import anbrain.qa.rococo.data.CountryEntity;
import anbrain.qa.rococo.grpc.*;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import lombok.extern.slf4j.Slf4j;

@GrpcService
@RequiredArgsConstructor
@Slf4j
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

    private final CountryDatabaseService countryDatabaseService;

    @Override
    public void getAllCountries(@Nonnull AllCountriesRequest request, @Nonnull StreamObserver<AllCountriesResponse> responseObserver) {
        log.debug("Получен запрос на получение списка стран. Страница: {}, Размер: {}",
                request.getPage(), request.getSize());

        PageRequest pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<CountryEntity> page = countryDatabaseService.getAllCountries(pageable);

        log.info("Возвращено {} стран из {}", page.getContent().size(), page.getTotalElements());
        responseObserver.onNext(buildAllCountriesResponse(page));
        responseObserver.onCompleted();
        log.debug("Запрос getAllCountries успешно завершен");
    }

    @Nonnull
    private AllCountriesResponse buildAllCountriesResponse(@Nonnull Page<CountryEntity> page) {
        return AllCountriesResponse.newBuilder()
                .addAllCountries(page.getContent().stream()
                        .map(this::toGrpcResponse)
                        .toList())
                .setTotalCount((int) page.getTotalElements())
                .build();
    }

    @Nonnull
    private Country toGrpcResponse(@Nonnull CountryEntity entity) {
        return Country.newBuilder()
                .setId(entity.getId().toString())
                .setName(entity.getName())
                .build();
    }
}