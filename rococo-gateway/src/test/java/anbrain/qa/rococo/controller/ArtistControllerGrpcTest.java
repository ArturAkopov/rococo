package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllArtistsResponse;
import anbrain.qa.rococo.grpc.ArtistResponse;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;


import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadProtoResponse;
import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadRequestJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.Status.*;


@Tag("ContractTest")
class ArtistControllerGrpcTest extends BaseControllerTest {

    private final String artistID = "104f76ce-0508-49d4-8967-fdf1ebb8cf45";

    private WireMockGrpcService mockArtistService;

    @BeforeEach
    void beforeEach() {
        mockArtistService = new WireMockGrpcService(
                WireMock.create().port(ARTIST_WIREMOCK_PORT).build(),
                ARTIST_GRPC_SERVICE_NAME
        );
    }

    @AfterEach
    void afterEach() {
        mockArtistService.resetAll();
    }

    @Test
    void getAllArtists_ShouldReturnArtistsFromGrpc() throws Exception {

        final AllArtistsResponse response = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "all_artist_response.json",
                AllArtistsResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("GetAllArtists")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is("104f76ce-0508-49d4-8967-fdf1ebb8cf45")))
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Шишкин")))
                .andExpect(jsonPath("$.content[1].id", Matchers.is("19bbbbb8-b687-4eec-8ba0-c8917c0a58a3")))
                .andExpect(jsonPath("$.content[1].name", Matchers.is("Ренуар")))
                .andExpect(jsonPath("$.content[2].id", Matchers.is("5a486b2f-c361-459e-bd3f-60692a635ea9")))
                .andExpect(jsonPath("$.content[2].name", Matchers.is("Левитан")));
    }

    @Test
    void getArtistById_ShouldReturnArtistFromGrpc() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist/{id}", artistID)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(artistID)))
                .andExpect(jsonPath("$.name", Matchers.is("Шишкин")))
                .andExpect(jsonPath("$.biography", Matchers.is(
                        "Русский живописец, рисовальщик и гравёр-офортист, один из главных мастеров реалистического пейзажа второй половины XIX века")))
                .andExpect(jsonPath("$.photo", Matchers.startsWith("data:image/jpeg;base64")));
    }

    @Test
    void searchArtistsByName_ShouldReturnArtistsFromGrpc() throws Exception {

        final AllArtistsResponse response = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "search_artists_response.json",
                AllArtistsResponse::newBuilder
        );

        mockArtistService.stubFor(
                WireMockGrpc.method("SearchArtistsByName")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("name", "Шиш")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", Matchers.is("Шишкин")));
    }

    @Test
    void createArtist_ShouldReturnCreatedArtist() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "create_artist_response.json",
                ArtistResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + ARTIST_REQUEST_PATH + "create_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("CreateArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(post("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(artistID)))
                .andExpect(jsonPath("$.biography", Matchers.is("Биография нового художника")))
                .andExpect(jsonPath("$.name", Matchers.is("Новый художник")));
    }

    @Test
    void updateArtist_ShouldReturnUpdatedArtist() throws Exception {

        final ArtistResponse response = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "update_artist_response.json",
                ArtistResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + ARTIST_REQUEST_PATH + "update_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("UpdateArtist")
                        .willReturn(WireMockGrpc.message(response)));

        mockMvc.perform(put("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is("104f76ce-0508-49d4-8967-fdf1ebb8cf45")))
                .andExpect(jsonPath("$.biography", Matchers.is("Обновленная биография")))
                .andExpect(jsonPath("$.name", Matchers.is("Обновленный Шишкин")));
    }

    @Test
    void getArtistById_ShouldReturn404WhenArtistNotFound() throws Exception {
        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(NOT_FOUND, "Художник с ID " + artistID + " не найден"));

        mockMvc.perform(get("/api/artist/{id}", artistID)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/artist/" + artistID)))
                .andExpect(jsonPath("$.error.errors[0].reason",
                        Matchers.is("Not Found")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Художник с ID " + artistID + " не найден")));
    }

    @Test
    void getArtistById_ShouldReturn400WhenInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/artist/{id}", "invalid-uuid")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/artist/invalid-uuid")))
                .andExpect(jsonPath("$.error.errors[0].reason",
                        Matchers.is("Bad Request")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Неверный формат ID музея: invalid-uuid")));
    }

    @Test
    void createArtist_ShouldReturn409WhenArtistExists() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + ARTIST_REQUEST_PATH + "create_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("CreateArtist")
                        .willReturn(ALREADY_EXISTS, "Артист уже существует"));

        mockMvc.perform(post("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code", Matchers.is("409 CONFLICT")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/artist")))
                .andExpect(jsonPath("$.error.errors[0].reason",
                        Matchers.is("Conflict")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Создание художника с такими параметрами уже существует: Новый художник")));
    }

    @Test
    void updateArtist_ShouldReturn404WhenArtistNotFound() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + ARTIST_REQUEST_PATH + "update_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("UpdateArtist")
                        .willReturn(NOT_FOUND, "Художник не найден"));

        mockMvc.perform(put("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/artist")))
                .andExpect(jsonPath("$.error.errors[0].reason",
                        Matchers.is("Not Found")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Обновление художника с ID 104f76ce-0508-49d4-8967-fdf1ebb8cf45 не найден")));
    }

    @Test
    void createArtist_ShouldReturn400WhenBiographyIsTooLong() throws Exception {
        String longBio = "a".repeat(2001);
        String requestJson = String.format("{\"name\":\"Valid name\", \"biography\":\"%s\", \"photo\":\"data:image/jpeg;base64,valid\"}", longBio);

        mockMvc.perform(post("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")));
    }

    @Test
    void getAllArtists_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockArtistService.stubFor(
                WireMockGrpc.method("GetAllArtists")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message", Matchers.is("Сервис временно недоступен")));
    }

    @Test
    void getArtistById_ShouldReturn504WhenGrpcTimeout() throws Exception {
        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(DEADLINE_EXCEEDED, "Таймаут"));

        mockMvc.perform(get("/api/artist/{id}", artistID)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message", Matchers.is("Превышено время ожидания ответа от сервиса")));
    }

    @Test
    void searchArtistsByName_ShouldReturn400WhenNameIsBlank() throws Exception {
        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("name", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")));
    }

    @Test
    void getAllArtists_ShouldReturn500WhenUnexpectedGrpcError() throws Exception {
        mockArtistService.stubFor(
                WireMockGrpc.method("GetAllArtists")
                        .willReturn(INTERNAL, "Внутренняя ошибка"));

        mockMvc.perform(get("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.errors[0].message", Matchers.containsString("Произошла внутренняя ошибка сервера")));
    }

    @Test
    void getAllArtists_ShouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/artist")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createArtist_ShouldReturn403WhenForbidden() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + ARTIST_REQUEST_PATH + "create_artist_request.json");

        mockArtistService.stubFor(
                WireMockGrpc.method("CreateArtist")
                        .willReturn(WireMockGrpc.Status.PERMISSION_DENIED, "Доступ запрещен"));

        mockMvc.perform(post("/api/artist")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.errors[0].message", Matchers.is("Доступ запрещен")));
    }
}
