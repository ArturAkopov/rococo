package anbrain.qa.rococo.service.grpc;

import anbrain.qa.rococo.grpc.*;
import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.page.RestPage;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static anbrain.qa.rococo.exception.GrpcExceptionHandler.handleGrpcException;

@Slf4j
@Service
public class CountryGrpcClient {

    @GrpcClient("rococo-museum")
    private CountryServiceGrpc.CountryServiceBlockingStub countryStub;

    public RestPage<CountryJson> getAllCountries(@Nonnull Pageable pageable) {
        try {
            log.info("Запрос всех стран, страница {}, размер {}", pageable.getPageNumber(), pageable.getPageSize());

            AllCountriesResponse response = countryStub.getAllCountries(
                    AllCountriesRequest.newBuilder()
                            .setPage(pageable.getPageNumber())
                            .setSize(pageable.getPageSize())
                            .build());

            List<CountryJson> countries = response.getCountriesList().stream()
                    .map(this::toCountryJson)
                    .collect(Collectors.toList());

            log.info("Получено {} стран из {}", countries.size(), response.getTotalCount());

            return new RestPage<>(
                    countries,
                    PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                    response.getTotalCount());
        } catch (StatusRuntimeException e) {
            log.error("Ошибка при получении списка стран: {}", e.getStatus().getDescription());
            throw handleGrpcException(e, "Страны", "страница " + pageable.getPageNumber());
        }
    }

    @Nonnull
    private CountryJson toCountryJson(@Nonnull Country country) {
        return new CountryJson(
                UUID.fromString(country.getId()),
                country.getName()
        );
    }
}