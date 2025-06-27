package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllPaintingsResponse;
import anbrain.qa.rococo.grpc.ArtistResponse;
import anbrain.qa.rococo.grpc.MuseumResponse;
import anbrain.qa.rococo.grpc.PaintingResponse;
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
class PaintingControllerGrpcTest extends BaseControllerTest {

    private final String paintingId = "104f76ce-0508-49d4-8967-fdf1ebb8cf65";
    private final String artistId = "104f76ce-0508-49d4-8967-fdf1ebb8cf45";
    private final String museumId = "3b785453-0d5b-4328-8380-5f226cb4dd5a";

    private WireMockGrpcService mockPaintingService;
    private WireMockGrpcService mockArtistService;
    private WireMockGrpcService mockMuseumService;

    @BeforeEach
    void beforeEach() {
        mockPaintingService = new WireMockGrpcService(
                WireMock.create().port(PAINTING_WIREMOCK_PORT).build(),
                PAINTING_GRPC_SERVICE_NAME
        );

        mockArtistService = new WireMockGrpcService(
                WireMock.create().port(ARTIST_WIREMOCK_PORT).build(),
                ARTIST_GRPC_SERVICE_NAME
        );

        mockMuseumService = new WireMockGrpcService(
                WireMock.create().port(MUSEUM_WIREMOCK_PORT).build(),
                MUSEUM_GRPC_SERVICE_NAME
        );
    }

    @AfterEach
    void afterEach() {
        mockPaintingService.resetAll();
        mockArtistService.resetAll();
        mockMuseumService.resetAll();
    }

    @Test
    void getPainting_ShouldReturnPaintingFromGrpc() throws Exception {
        final PaintingResponse paintingResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "painting_response.json",
                PaintingResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPainting")
                        .willReturn(WireMockGrpc.message(paintingResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(get("/api/painting/{id}", paintingId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(paintingId)))
                .andExpect(jsonPath("$.title", Matchers.is("Звездная ночь")))
                .andExpect(jsonPath("$.description", Matchers.is("Знаменитая картина Ван Гога")))
                .andExpect(jsonPath("$.content", Matchers.startsWith("data:image/jpeg;base64")))
                .andExpect(jsonPath("$.artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.museum.id", Matchers.is(museumId)));
    }

    @Test
    void getAllPaintings_ShouldReturnPaintingsFromGrpc() throws Exception {
        final AllPaintingsResponse paintingsResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "all_paintings_response.json",
                AllPaintingsResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        mockPaintingService.stubFor(
                WireMockGrpc.method("GetAllPaintings")
                        .willReturn(WireMockGrpc.message(paintingsResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is(paintingId)))
                .andExpect(jsonPath("$.content[0].title", Matchers.is("Звездная ночь")))
                .andExpect(jsonPath("$.content[0].artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.content[0].museum.id", Matchers.is(museumId)));
    }

    @Test
    void getPaintingsByArtist_ShouldReturnPaintingsFromGrpc() throws Exception {
        final AllPaintingsResponse paintingsResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "paintings_by_artist_response.json",
                AllPaintingsResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPaintingsByArtist")
                        .willReturn(WireMockGrpc.message(paintingsResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(get("/api/painting/author/{artistId}", artistId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.content[0].title", Matchers.is("Звездная ночь")))
                .andExpect(jsonPath("$.content[0].museum.id", Matchers.is(museumId)));
    }

    @Test
    void createPainting_ShouldReturnCreatedPainting() throws Exception {
        final PaintingResponse paintingResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "create_painting_response.json",
                PaintingResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + PAINTING_REQUEST_PATH + "create_painting_request.json");

        mockPaintingService.stubFor(
                WireMockGrpc.method("CreatePainting")
                        .willReturn(WireMockGrpc.message(paintingResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(post("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", Matchers.is(paintingId)))
                .andExpect(jsonPath("$.title", Matchers.is("Новая картина")))
                .andExpect(jsonPath("$.description", Matchers.is("Описание новой картины")))
                .andExpect(jsonPath("$.artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.museum.id", Matchers.is(museumId)));
    }

    @Test
    void updatePainting_ShouldReturnUpdatedPainting() throws Exception {
        final PaintingResponse paintingResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "update_painting_response.json",
                PaintingResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        final String requestJson = loadRequestJson(WIREMOCK_ROOT + PAINTING_REQUEST_PATH + "update_painting_request.json");

        mockPaintingService.stubFor(
                WireMockGrpc.method("UpdatePainting")
                        .willReturn(WireMockGrpc.message(paintingResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(put("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Matchers.is(paintingId)))
                .andExpect(jsonPath("$.title", Matchers.is("Обновленная звездная ночь")))
                .andExpect(jsonPath("$.description", Matchers.is("Новое описание")))
                .andExpect(jsonPath("$.artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.museum.id", Matchers.is(museumId)));
    }

    @Test
    void getPainting_ShouldReturn404WhenPaintingNotFound() throws Exception {
        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPainting")
                        .willReturn(NOT_FOUND, "Картина с ID " + paintingId + " не найдена"));

        mockMvc.perform(get("/api/painting/{id}", paintingId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/painting/" + paintingId)))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Картина с ID " + paintingId + " не найден")));
    }

    @Test
    void getPainting_ShouldReturn400WhenInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/painting/{id}", "invalid-uuid")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")))
                .andExpect(jsonPath("$.error.errors[0].domain",
                        Matchers.is("/api/painting/invalid-uuid")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Неверный формат ID картины")));
    }

    @Test
    void createPainting_ShouldReturn409WhenPaintingExists() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + PAINTING_REQUEST_PATH + "create_painting_request.json");

        mockPaintingService.stubFor(
                WireMockGrpc.method("CreatePainting")
                        .willReturn(ALREADY_EXISTS, "Картина уже существует"));

        mockMvc.perform(post("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code", Matchers.is("409 CONFLICT")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Создание картины с такими параметрами уже существует: Новая картина")));
    }

    @Test
    void updatePainting_ShouldReturn404WhenPaintingNotFound() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + PAINTING_REQUEST_PATH + "update_painting_request.json");

        mockPaintingService.stubFor(
                WireMockGrpc.method("UpdatePainting")
                        .willReturn(NOT_FOUND, "Картина не найдена"));

        mockMvc.perform(put("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code", Matchers.is("404 NOT_FOUND")))
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Обновление картины с ID " + paintingId + " не найден")));
    }

    @Test
    void getAllPaintings_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockPaintingService.stubFor(
                WireMockGrpc.method("GetAllPaintings")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Сервис временно недоступен")));
    }

    @Test
    void getPainting_ShouldReturn504WhenGrpcTimeout() throws Exception {
        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPainting")
                        .willReturn(DEADLINE_EXCEEDED, "Таймаут"));

        mockMvc.perform(get("/api/painting/{id}", paintingId)
                        .with(jwt().jwt(c -> c.claim("sub", "Artur"))))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Превышено время ожидания ответа от сервиса")));
    }

    @Test
    void getAllPaintings_ShouldReturn500WhenUnexpectedGrpcError() throws Exception {
        mockPaintingService.stubFor(
                WireMockGrpc.method("GetAllPaintings")
                        .willReturn(INTERNAL, "Внутренняя ошибка"));

        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Произошла внутренняя ошибка сервера")));
    }

    @Test
    void getAllPaintings_ShouldReturn401WhenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/painting")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createPainting_ShouldReturn403WhenForbidden() throws Exception {
        final String requestJson = loadRequestJson(WIREMOCK_ROOT + PAINTING_REQUEST_PATH + "create_painting_request.json");

        mockPaintingService.stubFor(
                WireMockGrpc.method("CreatePainting")
                        .willReturn(PERMISSION_DENIED, "Доступ запрещен"));

        mockMvc.perform(post("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.errors[0].message", Matchers.is("Доступ запрещен")));
    }

    @Test
    void getPaintingsByTitle_ShouldReturnPaintingsFromGrpc() throws Exception {
        final AllPaintingsResponse paintingsResponse = loadProtoResponse(
                WIREMOCK_ROOT + PAINTING_RESPONSE_PATH + "paintings_by_title_response.json",
                AllPaintingsResponse::newBuilder
        );

        final ArtistResponse artistResponse = loadProtoResponse(
                WIREMOCK_ROOT + ARTIST_RESPONSE_PATH + "artist_response.json",
                ArtistResponse::newBuilder
        );

        final MuseumResponse museumResponse = loadProtoResponse(
                WIREMOCK_ROOT + MUSEUM_RESPONSE_PATH + "museum_response.json",
                MuseumResponse::newBuilder
        );

        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPaintingsByTitle")
                        .willReturn(WireMockGrpc.message(paintingsResponse)));

        mockArtistService.stubFor(
                WireMockGrpc.method("GetArtist")
                        .willReturn(WireMockGrpc.message(artistResponse)));

        mockMuseumService.stubFor(
                WireMockGrpc.method("GetMuseum")
                        .willReturn(WireMockGrpc.message(museumResponse)));

        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "Звездная")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", Matchers.is(paintingId)))
                .andExpect(jsonPath("$.content[0].title", Matchers.is("Звездная ночь")))
                .andExpect(jsonPath("$.content[0].artist.id", Matchers.is(artistId)))
                .andExpect(jsonPath("$.content[0].museum.id", Matchers.is(museumId)));
    }

    @Test
    void getPaintingsByTitle_ShouldReturn400WhenTitleIsEmpty() throws Exception {
        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Название картины не может быть пустым")));
    }

    @Test
    void getPaintingsByTitle_ShouldReturn400WhenTitleIsBlank() throws Exception {
        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "   ")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.containsString("Название картины не может быть пустым")));
    }

    @Test
    void getPaintingsByTitle_ShouldReturn400WhenInvalidPageParams() throws Exception {
        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "Звездная")
                        .param("page", "-1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code", Matchers.is("400 BAD_REQUEST")));
    }

    @Test
    void getPaintingsByTitle_ShouldReturn503WhenGrpcServiceUnavailable() throws Exception {
        mockPaintingService.stubFor(
                WireMockGrpc.method("GetPaintingsByTitle")
                        .willReturn(UNAVAILABLE, "Сервис недоступен"));

        mockMvc.perform(get("/api/painting")
                        .with(jwt().jwt(c -> c.claim("sub", "Artur")))
                        .param("title", "Звездная")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error.errors[0].message",
                        Matchers.is("Сервис временно недоступен")));
    }
}