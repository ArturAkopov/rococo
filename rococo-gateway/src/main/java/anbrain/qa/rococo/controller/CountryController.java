package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.CountryJson;
import anbrain.qa.rococo.model.page.RestPage;
import anbrain.qa.rococo.service.grpc.CountryGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Country API", description = "Операции со странами")
@RestController
@RequestMapping("api/country")
public class CountryController {

    private final CountryGrpcClient countryGrpcClient;

    public CountryController(CountryGrpcClient countryGrpcClient) {
        this.countryGrpcClient = countryGrpcClient;
    }

    @Operation(summary = "Получить все страны с пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Страница со странами",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = RestPage.class)))
            })
    @GetMapping
    public ResponseEntity<RestPage<CountryJson>> getAllCountries(
            @Parameter(description = "Параметры пагинации") Pageable pageable) {
        return ResponseEntity.ok(countryGrpcClient.getAllCountries(pageable));
    }
}