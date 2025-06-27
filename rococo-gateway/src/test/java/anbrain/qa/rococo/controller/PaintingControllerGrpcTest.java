package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.grpc.AllPaintingsResponse;
import anbrain.qa.rococo.grpc.ArtistResponse;
import anbrain.qa.rococo.grpc.MuseumResponse;
import anbrain.qa.rococo.grpc.PaintingResponse;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpc;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadProtoResponse;
import static anbrain.qa.rococo.utils.ContractTestGrpcUtils.loadRequestJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.wiremock.grpc.dsl.WireMockGrpc.Status.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Tag("ContractTest")
class PaintingControllerGrpcTest {

    //Painting service
    private static final int PAINTING_WIREMOCK_PORT = 9094;
    private static final String PAINTING_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.PaintingService";
    private static final String WIREMOCK_ROOT = "src/test/resources/wiremock";
    private static final String PAINTING_REQUEST_PATH = "/request/painting/";
    private static final String PAINTING_RESPONSE_PATH = "/response/painting/";

    //Artist service
    private static final int ARTIST_WIREMOCK_PORT = 9091;
    private static final String ARTIST_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.ArtistService";
    private static final String ARTIST_RESPONSE_PATH = "/response/artist/";

    //Museum service
    private static final int MUSEUM_WIREMOCK_PORT = 9093;
    private static final String MUSEUM_GRPC_SERVICE_NAME = "anbrain.qa.rococo.grpc.MuseumService";
    private static final String MUSEUM_RESPONSE_PATH = "/response/museum/";

    private final String paintingId = "104f76ce-0508-49d4-8967-fdf1ebb8cf65";
    private final String artistId = "104f76ce-0508-49d4-8967-fdf1ebb8cf45";
    private final String museumId = "3b785453-0d5b-4328-8380-5f226cb4dd5a";

    private WireMockServer paintingWm;
    private WireMockServer artistWm;
    private WireMockServer museumWm;

    private WireMockGrpcService mockPaintingService;
    private WireMockGrpcService mockArtistService;
    private WireMockGrpcService mockMuseumService;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        //Painting service
        paintingWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(PAINTING_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        paintingWm.start();
        paintingWm.resetAll();

        mockPaintingService = new WireMockGrpcService(
                WireMock.create().port(PAINTING_WIREMOCK_PORT).build(),
                PAINTING_GRPC_SERVICE_NAME
        );
        mockPaintingService.resetAll();
        System.out.println("Painting server started on port " + paintingWm.port());

        //Artist service
        artistWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(ARTIST_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        artistWm.start();
        artistWm.resetAll();

        mockArtistService = new WireMockGrpcService(
                WireMock.create().port(ARTIST_WIREMOCK_PORT).build(),
                ARTIST_GRPC_SERVICE_NAME
        );
        mockArtistService.resetAll();
        System.out.println("Artist server started on port " + artistWm.port());

        //Museum service
        museumWm = new WireMockServer(
                WireMockConfiguration.wireMockConfig()
                        .port(MUSEUM_WIREMOCK_PORT)
                        .withRootDirectory(WIREMOCK_ROOT)
                        .extensions(new Jetty12GrpcExtensionFactory())
        );
        museumWm.start();
        museumWm.resetAll();

        mockMuseumService = new WireMockGrpcService(
                WireMock.create().port(MUSEUM_WIREMOCK_PORT).build(),
                MUSEUM_GRPC_SERVICE_NAME
        );
        mockMuseumService.resetAll();
        System.out.println("Museum server started on port " + museumWm.port());
    }

    @AfterEach
    void afterEach() {
        stopWireMockServer(paintingWm, "Painting");
        stopWireMockServer(artistWm, "Artist");
        stopWireMockServer(museumWm, "Museum");
    }

    private void stopWireMockServer(WireMockServer server, String serviceName) {
        System.out.println("Stopping " + serviceName + " WireMock server...");
        if (server != null) {
            if (server.isRunning()) {
                server.stop();
                System.out.println(serviceName + " WireMock server stopped.");
            } else {
                System.out.println(serviceName + " WireMock server was already stopped.");
            }
        }
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
                        Matchers.is("Картина с таким названием уже существует")));
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
}