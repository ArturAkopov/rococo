package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllCountriesResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadProtoResponse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.Status.*;

@Tag("ContractTest")
class CountryControllerGrpcTest extends BaseControllerTest {


    private WireMockGrpcService mockCountryService;

    @BeforeEach
    void beforeEach() {
        mockCountryService = new WireMockGrpcService(
                WireMock.create().port(MUSEUM_WIREMOCK_PORT).build(),
                COUNTRY_GRPC_SERVICE_NAME
        );
    }

    @AfterEach
    void afterEach() {
        mockCountryService.resetAll();
    }


    @Test
    void getAllCountries_ShouldReturnCountriesFromGrpc() throws Exception {
        final AllCountriesResponse response = loadProtoResponse(
                WIREMOCK_ROOT + COUNTRY_RESPONSE_PATH + "all_countries_response.json",
                AllCountriesResponse::newBuilder
        );

        mockCountryService.stubFor(
                WireMockGrpc.method("GetAllCountries")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/country")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is("11ee622c-cecf-75eb-8e63-0242ac110001")))
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Россия")))
                .andExpect(jsonPath("$.content[1].id", Matchers.is("11ee622c-cecf-75eb-8e63-0242ac110002")))
                .andExpect(jsonPath("$.content[1].name", Matchers.is("Франция")))
                .andExpect(jsonPath("$.content[2].id", Matchers.is("11ee622c-cecf-75eb-8e63-0242ac110003")))
                .andExpect(jsonPath("$.content[2].name", Matchers.is("Италия")));
    }

    @Test
    void getAllCountries_ShouldReturn400WhenInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/country")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/country")))
                .andExpect(jsonPath("$.error.errors[0].reason",
                        Matchers.is("Bad Request")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Ошибка при обработке Страны")));
    }

    @Test
    void getAllCountries_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockCountryService.stubFor(
                WireMockGrpc.method("GetAllCountries")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/country")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Сервис временно недоступен")));
    }

    @Test
    void getAllCountries_ShouldReturn504WhenGrpcTimeout() throws Exception {
        mockCountryService.stubFor(
                WireMockGrpc.method("GetAllCountries")
                        .willReturn(DEADLINE_EXCEEDED, "Таймаут"));

        mockMvc.perform(get("/api/country")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Превышено время ожидания ответа от сервиса")));
    }

    @Test
    void getAllCountries_ShouldReturn500WhenUnexpectedGrpcError() throws Exception {
        mockCountryService.stubFor(
                WireMockGrpc.method("GetAllCountries")
                        .willReturn(INTERNAL, "Внутренняя ошибка"));

        mockMvc.perform(get("/api/country")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Произошла внутренняя ошибка сервера")));
    }

    @Test
    void getAllCountries_ShouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/country")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }
}